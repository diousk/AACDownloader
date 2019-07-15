package com.example.downloader.worker

import android.content.Context
import androidx.work.*
import com.example.downloader.FileUtils
import com.example.downloader.Resource
import com.example.downloader.ResourceWorkData
import com.example.downloader.api.DownloadApi
import com.example.downloader.database.daos.ResourceDao
import com.example.downloader.database.entities.ResourceEntity
import com.example.downloader.worker.base.IWorkerFactory
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

// resource download worker
class DownloadWorker(
    private val context: Context, params: WorkerParameters,
    private val resourceDao: ResourceDao,
    private val downloadApi: DownloadApi
) : RxWorker(context, params) {

    override fun createWork(): Single<Result> {
        Timber.w("createWork runAttemptCount = $runAttemptCount")
        val resId = inputData.getInt(KEY_RES_ID, -1)
        val resUrl = inputData.getString(KEY_RES_URL)
        val resMd5 = inputData.getString(KEY_RES_MD5) ?: ""
        val resNumber = inputData.getInt(KEY_RES_NUMBER, 0)
        val totalNumber = inputData.getInt(KEY_RES_TOTAL_NUMBER, 0)
        val resourceWorkData = ResourceWorkData(resNumber, totalNumber, Resource(resId, resUrl, resMd5))
        Timber.w("resourceWorkData = $resourceWorkData")

        if (resId == -1 || resUrl.isNullOrEmpty()) {
            Timber.w("ignore invalid resource, resId = $resId, resUrl = $resUrl")
            return Single.just(Result.success())
        }

        if (runAttemptCount > MAX_RETRY) {
            Timber.e("too many failed attempts for $resId - $resUrl, give up")
            // emit success so that we can continue to download next resource
            return Single.just(Result.success())
        }

        val file = File(getResourceFilePath(resId, resUrl))

        // verify existing file or start download from network
        return verifyExistingResource(resourceDao, resourceWorkData)
            .flatMap { fileVerified ->
                if (fileVerified) { // treat as success
                    Timber.d("fileVerified for $resourceWorkData")
                    return@flatMap Single.just(true)
                }

                // start download
                Timber.d("start download to filePath ${file.absolutePath}")
                downloadAndSave(resUrl, file.absolutePath).doOnSuccess { success ->
                    Timber.d("download success = $success, $resNumber/$totalNumber")
                    if (success) {
                        val id = saveResourceToDb(resourceDao, resourceWorkData, file.absolutePath).blockingGet()
                        Timber.d("saveResourceToDb id = $id")
                    } else {
                        // delete file if failed to download
                        file.delete()
                    }
                }
            }
            .map { if (it) Result.success() else Result.retry() }
            .onErrorReturn { error ->
                Timber.d("download error: $error")
                Result.retry()
            }
    }

    // check if record exists in db and check if md5 matches
    // return Single<Boolean> to indicate file existing and verified
    private fun verifyExistingResource(resourceDao: ResourceDao, resource: ResourceWorkData): Single<Boolean> {
        return resourceDao.getResourceById(resource.resData.id)
            .map {
                val verifyMd5 = resource.resData.resourceMd5 ?: ""
                val recordFile = File(it.resourceFilePath)
                val md5Matches = FileUtils.checkMD5(verifyMd5, recordFile)
                if (recordFile.exists() && !md5Matches) {
                    Timber.d("recordFile exist but md5 not match, delete file. resource = $resource")
                    recordFile.delete()
                }
                return@map recordFile.exists() && md5Matches
            }
            .onErrorReturn {
                Timber.d("verifyExistingResource failed: $it")
                false
            }
            .subscribeOn(Schedulers.io())
    }

    // return Single<Boolean> to indicate download success
    private fun downloadAndSave(downloadUrl: String, filepath: String): Single<Boolean> {
        return downloadApi.download(downloadUrl)
            .map { FileUtils.saveResponseBodyToFile(it, filepath) { isStopped } }
    }

    private fun saveResourceToDb(resourceDao: ResourceDao, resource: ResourceWorkData, filepath: String): Single<Long> {
        val entity = ResourceEntity(
            resource.resData.id,
            resource.resData.resourceUrl!!,
            filepath,
            resource.resData.resourceMd5!!
        )
        Timber.d("saveResourceToDb: $entity")
        return resourceDao.insert(entity)
    }

    private fun getResourceFilePath(resId: Int, resUrl: String): String {
        val fileFolder = File(
            context.filesDir.absolutePath +
                    File.separator +
                    FOLDER_NAME_RES
        ).apply { mkdirs() }
        return fileFolder.absolutePath + File.separator + resId
    }

    class Factory @Inject constructor(
        private val resourceDao: Provider<ResourceDao>,
        private val downloadApi: Provider<DownloadApi>
    ) : IWorkerFactory {
        override fun create(context: Context, params: WorkerParameters): DownloadWorker {
            return DownloadWorker(context, params, resourceDao.get(), downloadApi.get())
        }
    }

    companion object {
        const val TAG = "DOWNLOAD_WORKER"
        private const val MAX_RETRY = 3

        private const val FOLDER_NAME_RES = "resources"
        private const val KEY_RES_ID = "key_res_id"
        private const val KEY_RES_URL = "key_res_url"
        private const val KEY_RES_MD5 = "key_res_md5"
        private const val KEY_RES_NUMBER = "key_res_no"
        private const val KEY_RES_TOTAL_NUMBER = "key_res_total"

        fun schedule(workManager: WorkManager, resources: List<ResourceWorkData>, replace: Boolean = false) {
            if (resources.isEmpty()) {
                Timber.d("resources empty")
                return
            }

            val workRequests = resources.map { resource ->
                Timber.d("resource - $resource")
                OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .setInputData(
                        workDataOf(
                            KEY_RES_ID to resource.resData.id,
                            KEY_RES_URL to resource.resData.resourceUrl,
                            KEY_RES_NUMBER to resource.resNumber,
                            KEY_RES_TOTAL_NUMBER to resource.totalNumber,
                            KEY_RES_MD5 to resource.resData.resourceMd5
                        )
                    )
                    .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.SECONDS)
                    .build()
            }.toMutableList()

            val policy = if (replace) {
                ExistingWorkPolicy.REPLACE
            } else {
                ExistingWorkPolicy.KEEP
            }
            val first = workRequests.removeAt(0)
            var continuation = workManager.beginUniqueWork(TAG, policy, first)
            workRequests.forEach { continuation = continuation.then(it) }
            continuation.enqueue()
        }

        fun scheduleOneshot(workManager: WorkManager, resource: ResourceWorkData) {
            if (resource.resData.id <= 0) {
                Timber.w("invalid resource id")
                return
            }

            val data = workDataOf(
                KEY_RES_ID to resource.resData.id,
                KEY_RES_URL to resource.resData.resourceUrl,
                KEY_RES_NUMBER to resource.resData,
                KEY_RES_TOTAL_NUMBER to resource.totalNumber,
                KEY_RES_MD5 to resource.resData.resourceMd5
            )
            val request = OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInputData(data)
                .build()
            workManager.enqueueUniqueWork(
                TAG + resource.resData.id,
                ExistingWorkPolicy.KEEP,
                request
            )
        }
    }
}
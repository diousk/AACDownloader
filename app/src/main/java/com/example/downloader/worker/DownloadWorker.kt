package com.example.downloader.worker

import android.content.Context
import androidx.work.*
import com.example.downloader.Data
import com.example.downloader.FileUtils
import com.example.downloader.api.DownloadApi
import com.example.downloader.worker.base.IWorkerFactory
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Provider

class DownloadWorker(
    context: Context, params: WorkerParameters,
    private val downloadApi: DownloadApi
) : RxWorker(context, params) {

    override fun createWork(): Single<Result> {
        val url = inputData.getString("myurl") ?: ""
        Timber.d("createWork with myurl ${url}, runAttemptCount = $runAttemptCount")
        Timber.d("createWork with urls ${inputData.getStringArray("urls")?.toMutableList()}")

        if (runAttemptCount > 3) {
            Timber.d("too many failed attempts, give up")
            return Single.just(Result.failure())
        }

        if (url.isEmpty()) {
            return Single.just(Result.success(workDataOf("url" to url)))
        }

        val file = File(FileUtils.getSavePath(url))
        Timber.d("filePath ${file.absolutePath}")

        return downloadApi.download(url)
            .map {
                FileUtils.saveToFile(it, file.absolutePath)
                Result.success(workDataOf("url" to url))
            }.onErrorReturn {
                Timber.d("catch error $it")
                Result.failure(workDataOf("url" to url))
            }
    }

    override fun onStopped() {
        Timber.d("worker onStopped")
    }

    class Factory @Inject constructor(
        private val downloadApi: Provider<DownloadApi>
    ) : IWorkerFactory {
        override fun create(context: Context, params: WorkerParameters): DownloadWorker {
            return DownloadWorker(context, params, downloadApi.get())
        }
    }

    companion object {
        const val TAG = "DOWNLOAD_WORKER"
        fun schedule(workManager: WorkManager, urls: List<String>) {
            // TODO: we should use another worker to check if all urls downloaded
            val beginRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                .setInputData(workDataOf("urls" to urls.toTypedArray()))
                .build()

            val workRequests = urls.map { url ->
                Timber.d("url - $url")
                OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                    .setInputData(workDataOf("myurl" to url))
                    .build()
            }

            var continuation = workManager.beginUniqueWork(
                TAG, ExistingWorkPolicy.REPLACE, beginRequest
            )
            workRequests.forEach { continuation = continuation.then(it) }
            continuation.enqueue()
        }
    }
}
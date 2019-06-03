package com.example.downloader.main

import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.downloader.FileUtils
import com.example.downloader.FileUtils.saveToFile
import com.example.downloader.api.DownloadApi
import io.reactivex.BackpressureOverflowStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.reactivestreams.Subscription
import timber.log.Timber
import java.io.File
import retrofit2.adapter.rxjava2.Result.response
import java.lang.RuntimeException
import androidx.work.OneTimeWorkRequest




class MainViewModel(
    private val downloadApi: DownloadApi
) : ViewModel() {
    val compositeDisposable = CompositeDisposable()

    fun download(url: String) {
        val filePath = FileUtils.getSavePath(url)
        Timber.d("filePath $filePath")
        val file = File(FileUtils.getSavePath(url))
        val disposable = downloadApi.download(url)
            .subscribe({
                Timber.d("response success, thread : ${Thread.currentThread().name}")
                saveToFile(it, filePath)
            }, {
                Timber.d("error $it")
            })
        compositeDisposable.add(disposable)
    }

    var sub: Subscription? = null
    fun simulateManyTask() {
        val intArray = IntArray(200) { it }.toTypedArray().toList()

        Flowable.fromIterable(intArray)
            .subscribeOn(Schedulers.io())
            .onBackpressureBuffer()
            .doOnNext {
                Timber.d("on sleep $it")
                Thread.sleep(1000)
            }
            .observeOn(Schedulers.computation(), true, 1)
            .subscribe({
                Timber.d("on next $it")
            }, {
                Timber.d("on error $it")
            }, {
                Timber.d("on done")
                // or remove live data observer here?
            }, {
                Timber.d("on sub")
                sub = it
//                sub?.request(3)
            })

//        val cacheCleanupTask = OneTimeWorkRequest.Builder(MyCacheCleanupWorker::class.java!!)
//            .addTag("cleanup")
//            .build()
//        WorkManager.getInstance().getWorkInfosByTagLiveData().observeForever {
//           // if all finished, remove observer
//            WorkManager.getInstance().enqueueUniqueWork()
//            it[0].
//        }
    }

    fun req() {
        sub?.request(1)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
    }
}
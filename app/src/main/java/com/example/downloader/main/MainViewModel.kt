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
import com.example.downloader.Data
import com.example.downloader.worker.DownloadWorker


class MainViewModel : ViewModel() {
    val compositeDisposable = CompositeDisposable()

    init {
        WorkManager.getInstance().getWorkInfosForUniqueWorkLiveData(DownloadWorker.TAG)
            .observeForever {
                it.forEachIndexed { index, workInfo ->
                    Timber.d("state of worker: ${index}, ${workInfo.outputData.getString("url")}, ${workInfo.state}")
                }
            }
    }

    fun download(url: String) {
        Timber.d("schedule download worker")
        DownloadWorker.schedule(WorkManager.getInstance(), Data.sampleUrls.toMutableList())
    }

    fun cancel() {
        WorkManager.getInstance().cancelUniqueWork(DownloadWorker.TAG)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        WorkManager.getInstance().pruneWork()
    }
}
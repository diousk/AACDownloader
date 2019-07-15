package com.example.downloader.main

import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import com.example.downloader.*
import com.example.downloader.worker.DownloadWorker
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class MainViewModel : ViewModel() {
    val compositeDisposable = CompositeDisposable()

    init {
        WorkManager.getInstance().getWorkInfosForUniqueWorkLiveData(DownloadWorker.TAG)
            .observeForever {
                it.forEachIndexed { index, workInfo ->
                    Timber.d(
                        "worker: ${index}, " +
                                "${workInfo.state}, " +
                                "${workInfo.tags}"
                    )
                }
            }
    }

    fun download(jsonData: String) {
        val typeData = Types.newParameterizedType(List::class.java, Resource::class.java)
        val giftAdapter = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            .adapter<List<Resource>>(typeData)

        val disposable = Single.just(jsonData)
            .subscribeOn(Schedulers.io())
            .map { giftAdapter.fromJson(it) }
            .map { list ->
                val giftResourceList = list.filter { !it.resourceUrl.isNullOrEmpty() }
                val giftResourceListSize = giftResourceList.size
                val indexedList = MutableList(giftResourceListSize) { index ->
                    ResourceWorkData(index+1, giftResourceListSize, giftResourceList[index])
                }
                indexedList
            }
            .subscribe({
                Timber.d("schedule download worker")
                DownloadWorker.schedule(WorkManager.getInstance(), it)
            }, {
                Timber.d("schedule download worker err: $it")
            })
        compositeDisposable.add(disposable)
    }

    fun cancel() {
        WorkManager.getInstance().cancelUniqueWork(DownloadWorker.TAG)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        WorkManager.getInstance().pruneWork()
    }
}
package com.example.downloader.di

import com.example.downloader.worker.DownloadWorker
import com.example.downloader.worker.base.IWorkerFactory
import com.example.downloader.worker.base.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class WorkerBinds {
    @Binds
    @IntoMap
    @WorkerKey(DownloadWorker::class)
    abstract fun bindDownloadWorker(factory: DownloadWorker.Factory): IWorkerFactory
}
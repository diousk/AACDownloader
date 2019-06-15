package com.example.downloader.di

import androidx.work.WorkManager
import com.example.downloader.di.WorkerBinds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [WorkerBinds::class])
class WorkerModule {
    @Provides
    @Singleton
    fun provideWorkManager(): WorkManager = WorkManager.getInstance()
}
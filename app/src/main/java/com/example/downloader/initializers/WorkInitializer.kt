package com.example.downloader.initializers

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.example.downloader.worker.base.DaggerAwareWorkerFactory
import javax.inject.Inject

class WorkInitializer @Inject constructor(
    private val workerFactory: DaggerAwareWorkerFactory
) : AppInitializer {

    override fun init(application: Application) {
        val wmConfig = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(application, wmConfig)
    }
}
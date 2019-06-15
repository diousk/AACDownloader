package com.example.downloader.worker.base

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

interface IWorkerFactory {
    fun create(context: Context, params: WorkerParameters): ListenableWorker
}
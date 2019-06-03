package com.example.downloader

import com.example.downloader.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber

class App: DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        FileUtils.filesDirPath = filesDir.absolutePath
        FileUtils.cacheDirPath = cacheDir.absolutePath
        Timber.plant(Timber.DebugTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}
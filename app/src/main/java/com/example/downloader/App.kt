package com.example.downloader

import com.example.downloader.di.DaggerAppComponent
import com.example.downloader.initializers.AppInitializers
import dagger.Lazy
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import timber.log.Timber
import javax.inject.Inject

class App: DaggerApplication() {
    @Inject
    lateinit var appInitializers: Lazy<AppInitializers>
    override fun onCreate() {
        super.onCreate()
        FileUtils.filesDirPath = filesDir.absolutePath
        FileUtils.cacheDirPath = cacheDir.absolutePath
        Timber.plant(Timber.DebugTree())
        appInitializers.get().init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }
}
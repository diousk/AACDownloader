package com.example.downloader.initializers

import android.app.Application

interface AppInitializer {
    fun init(application: Application)
}
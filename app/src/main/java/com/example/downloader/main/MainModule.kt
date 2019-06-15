package com.example.downloader.main

import androidx.lifecycle.ViewModel
import com.example.downloader.api.DownloadApi
import com.example.downloader.di.injection.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class MainModule {
    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideMainViewModel() :ViewModel {
        return MainViewModel()
    }

}
package com.example.downloader.main

import androidx.lifecycle.ViewModel
import com.example.downloader.api.DownloadApi
import com.kingkonglive.android.utils.injection.ViewModelKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class MainModule {
    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideMainViewModel(downloadApi: DownloadApi) :ViewModel {
        return MainViewModel(downloadApi)
    }

}
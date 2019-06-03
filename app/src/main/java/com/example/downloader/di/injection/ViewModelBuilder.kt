package com.example.downloader.di.injection

import androidx.lifecycle.ViewModelProvider
import com.kingkonglive.android.utils.injection.DaggerAwareViewModelFactory
import dagger.Binds
import dagger.Module

@Module
internal abstract class ViewModelBuilder {
    @Binds
    internal abstract fun bindViewModelFactory(factory: DaggerAwareViewModelFactory):
            ViewModelProvider.Factory
}
package com.example.downloader.di

import com.example.downloader.initializers.AppInitializer
import com.example.downloader.initializers.WorkInitializer
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet

@Module
abstract class AppModuleBinds {

    @Binds
    @IntoSet
    abstract fun provideWorkInitializer(binds: WorkInitializer): AppInitializer
}
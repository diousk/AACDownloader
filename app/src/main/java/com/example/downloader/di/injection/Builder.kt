package com.example.downloader.di.injection

import com.example.downloader.main.MainActivity
import com.example.downloader.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuilderModule {
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun bindMainActivity(): MainActivity
}
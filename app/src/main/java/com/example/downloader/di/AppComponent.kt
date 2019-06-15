package com.example.downloader.di

import com.example.downloader.App
import com.example.downloader.di.injection.BuilderModule
import com.example.downloader.di.injection.ViewModelBuilder
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        BuilderModule::class,
        ViewModelBuilder::class,
        NetModule::class,
        AppModule::class,
        WorkerModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Factory
    abstract class Builder : AndroidInjector.Factory<App>
}
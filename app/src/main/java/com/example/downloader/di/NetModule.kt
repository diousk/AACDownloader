package com.example.downloader.di

import android.content.Context
import com.example.downloader.api.DownloadApi
import com.example.downloader.R
import com.example.downloader.const.ApiConst
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetModule {
    // define log interceptor
    @Provides
    fun provideLogInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(ApiConst.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(ApiConst.CONN_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(ApiConst.IDLE_CONNECTION_NUMBER, ApiConst.IMAGE_KEEP_ALIVE_TIMEOUT, TimeUnit.SECONDS))
            .retryOnConnectionFailure(false)
            .protocols(listOf(Protocol.HTTP_1_1))
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiRetrofit(context: Context, okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        val awsAddress = context.getString(R.string.api_host)
        return Retrofit.Builder()
            .baseUrl(awsAddress)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .build()
    }

    @Provides
    @Singleton
    fun provideDownloadApi(retrofit: Retrofit): DownloadApi {
        return retrofit.create(DownloadApi::class.java)
    }
}
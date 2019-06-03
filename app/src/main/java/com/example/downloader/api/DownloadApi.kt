package com.example.downloader.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadApi {
    @Streaming
    @GET
    fun download(@Url url: String): Single<ResponseBody>
}
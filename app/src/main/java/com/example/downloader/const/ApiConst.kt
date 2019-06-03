package com.example.downloader.const

object ApiConst {
    const val API_CLIENT_NAME = "apiClient"
    const val IMAGE_CLIENT_NAME = "imageClient"

    const val API_RETROFIT_NAME = "apiRetrofit"
    const val PASSPORT_RETROFIT_NAME = "passportRetrofit"
    const val KK_API_RETROFIT_NAME = "kkApiRetrofit"

    const val HEADER_INTERCEPTOR = "headerInterceptor"
    const val RESPONSE_INTERCEPTOR = "responseInterceptor"
    const val LOG_INTERCEPTOR = "logInterceptor"

    const val READ_TIMEOUT_SECONDS = 15L
    const val CONN_TIMEOUT_SECONDS = 30L
    const val MAX_PARALLEL_REQUESTS = 64
    const val MAX_PARALLEL_REQUESTS_PER_HOST = 16
    const val IDLE_CONNECTION_NUMBER = 5
    const val KEEP_ALIVE_TIMEOUT = 50L
    const val IMAGE_KEEP_ALIVE_TIMEOUT = 25L
}
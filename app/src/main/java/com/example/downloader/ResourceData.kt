package com.example.downloader

import com.squareup.moshi.Json

// from response json to object
data class Resource(
    @Json(name = "id") val id: Int,
    @Json(name = "resource") val resourceUrl: String? = null,
    @Json(name = "resource_md5") val resourceMd5: String? = ""
)

// for worker data
data class ResourceWorkData(
    val resNumber: Int,
    val totalNumber: Int,
    val resData: Resource
)
package com.example.imagedownloadertest.data.api

import com.example.imagedownloadertest.data.model.ImageListResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("/api/v2/content/misc/media-coverages?limit=100")
    fun getImageListResponse():Deferred<Response<ImageListResponse>>
}
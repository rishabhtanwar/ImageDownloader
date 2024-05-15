package com.example.imagedownloadertest.data.api

import com.example.imagedownloadertest.data.model.Resource
import com.example.imagedownloadertest.data.model.ImageListResponse

interface ApiHelper {
   suspend fun getImageListResponse(): Resource<ImageListResponse>
}
package com.example.imagedownloadertest.data.api

import com.example.imagedownloadertest.data.model.Resource
import com.example.imagedownloadertest.data.model.ImageListResponse

class ApiHelperImpl(private val apiService: ApiService) :
    ApiHelper {

    override suspend fun getImageListResponse(): Resource<ImageListResponse> {
       val response = apiService.getImageListResponse().await()
        val responseBody = response.body()
        return if (response.isSuccessful){
            Resource.Success(responseBody)
        }else{
            Resource.Error("Something went wrong")
        }
    }
}
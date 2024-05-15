package com.example.imagedownloadertest.data.repository

import android.util.Log
import com.example.imagedownloadertest.data.api.ApiHelper
import com.example.imagedownloadertest.data.model.ImageListResponse
import com.example.imagedownloadertest.data.model.Resource
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class ImageDownloaderRepo(private val apiHelper: ApiHelper) {

    fun getImageListResponse() = flow<Resource<ImageListResponse>> {
        emit(Resource.Loading())
        val response = apiHelper.getImageListResponse()
        emit(response)
    }.catch {
        Log.e("TAG", "getResponse: ${it.printStackTrace()}" )
        emit(Resource.Error(error = "Something went wrong"))
    }
}
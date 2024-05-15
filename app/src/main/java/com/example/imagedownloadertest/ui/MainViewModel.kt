package com.example.imagedownloadertest.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagedownloadertest.data.model.ImageListResponse
import com.example.imagedownloadertest.data.model.Resource
import com.example.imagedownloadertest.data.repository.ImageDownloaderRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repo: ImageDownloaderRepo
) : ViewModel() {

    private val _imageListResponse = MutableLiveData<Resource<ImageListResponse>>()

    val imageListResponse: LiveData<Resource<ImageListResponse>> get() = _imageListResponse


    fun getImageListResponse() {
        viewModelScope.launch {
            repo.getImageListResponse().flowOn(Dispatchers.IO).collect() {
                _imageListResponse.value = it
            }
        }
    }

}
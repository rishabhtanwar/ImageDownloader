package com.example.imagedownloadertest.di

import com.example.imagedownloadertest.data.api.ApiHelper
import com.example.imagedownloadertest.data.api.ApiHelperImpl
import com.example.imagedownloadertest.data.repository.ImageDownloaderRepo
import org.koin.dsl.module

val repoModule = module {
    factory { ImageDownloaderRepo(get()) }
    factory <ApiHelper> { return@factory ApiHelperImpl(get()) }
}
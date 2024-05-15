package com.example.imagedownloadertest

import android.app.Application
import com.example.imagedownloadertest.di.appModule
import com.example.imagedownloadertest.di.repoModule
import com.example.imagedownloadertest.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ImageLoaderApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ImageLoaderApp)
            modules(arrayListOf(appModule, repoModule, viewModelModule))
        }
    }
}
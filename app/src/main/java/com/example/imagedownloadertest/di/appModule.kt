package com.example.imagedownloadertest.di

import com.example.imagedownloadertest.data.api.ApiService
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { getOkHttpClient() }
    single { getRetrofitInstance(get()) }
    single { getApiService(get()) }
}

fun getOkHttpClient(): OkHttpClient {
    val dispatcher = Dispatcher()
    dispatcher.maxRequests = 1
    dispatcher.maxRequestsPerHost = 1

    val logInterceptor = HttpLoggingInterceptor()
    logInterceptor.level = HttpLoggingInterceptor.Level.BODY

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logInterceptor)
    okHttpClient.retryOnConnectionFailure(true)

    return okHttpClient.build()
}


fun getRetrofitInstance(okHttpClient: OkHttpClient): Retrofit {
    val gson = GsonBuilder().serializeNulls()
        .create()
    return Retrofit.Builder()
        .baseUrl("https://acharyaprashant.org")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttpClient).build()
}

fun getApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
}



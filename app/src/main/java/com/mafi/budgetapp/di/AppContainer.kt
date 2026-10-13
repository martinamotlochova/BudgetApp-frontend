package com.mafi.budgetapp.di

import android.content.Context
import com.mafi.budgetapp.data.local.TokenDataStore
import com.mafi.budgetapp.data.remote.AuthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val baseUrl = "http://10.0.2.2:5113/"


    val tokenDataStore = TokenDataStore(context)

    // 2. interceptors (AuthInterceptor will be added here in task 2)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
}
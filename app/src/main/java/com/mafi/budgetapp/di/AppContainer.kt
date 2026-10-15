package com.mafi.budgetapp.di

import android.content.Context
import com.mafi.budgetapp.data.local.TokenDataStore
import com.mafi.budgetapp.data.remote.AuthApiService
import com.mafi.budgetapp.data.remote.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.mafi.budgetapp.BuildConfig

class AppContainer(context: Context) {

    private val baseUrl = "http://10.0.2.2:5113/"


    val tokenDataStore = TokenDataStore(context)

    private val authInterceptor = AuthInterceptor(tokenDataStore)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        redactHeader("Authorization")
    }


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()


    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
}
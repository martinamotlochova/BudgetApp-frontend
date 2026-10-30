package com.mafi.budgetapp.di

import android.content.Context
import com.mafi.budgetapp.BuildConfig
import com.mafi.budgetapp.data.local.CryptoManager
import com.mafi.budgetapp.data.local.TokenDataStore
import com.mafi.budgetapp.data.remote.AuthApiService
import com.mafi.budgetapp.data.remote.AuthInterceptor
import com.mafi.budgetapp.data.remote.TokenAuthenticator                // NEW
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val baseUrl = "http://10.0.2.2:5113/"

    // 1. local storage
    private val cryptoManager = CryptoManager()                          // NEW
    val tokenDataStore = TokenDataStore(context, cryptoManager)          // CHANGED

    // 2. logging (shared by both HTTP clients)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        redactHeader("Authorization")
    }

    // 3. refresh client: no auth interceptor, no authenticator (prevents loops)   // NEW
    private val refreshOkHttpClient = OkHttpClient.Builder()                       // NEW
        .addInterceptor(loggingInterceptor)                                        // NEW
        .build()                                                                   // NEW

    private val refreshApiService: AuthApiService = Retrofit.Builder()            // NEW
        .baseUrl(baseUrl)                                                          // NEW
        .client(refreshOkHttpClient)                                               // NEW
        .addConverterFactory(GsonConverterFactory.create())                        // NEW
        .build()                                                                   // NEW
        .create(AuthApiService::class.java)                                        // NEW

    // 4. auth components
    private val authInterceptor = AuthInterceptor(tokenDataStore)
    private val tokenAuthenticator = TokenAuthenticator(tokenDataStore, refreshApiService)   // NEW

    // 5. main HTTP client
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .authenticator(tokenAuthenticator)                                         // NEW
        .build()

    // 6. Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 7. API services (used by the rest of the app)
    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
}
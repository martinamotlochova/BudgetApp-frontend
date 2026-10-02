package com.mafi.budgetapp.data.remote

import com.mafi.budgetapp.data.remote.dto.LoginRequest
import com.mafi.budgetapp.data.remote.dto.LoginResponse
import com.mafi.budgetapp.data.remote.dto.RefreshRequest
import com.mafi.budgetapp.data.remote.dto.RegisterRequest
import com.mafi.budgetapp.data.remote.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<LoginResponse>
}
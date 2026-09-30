package com.mafi.budgetapp.data.remote.dto

data class LoginResponse (
    val accessToken : String,
    val accessTokenExpiresAt : String,
    val refreshToken : String,
    val user : UserResponse
)
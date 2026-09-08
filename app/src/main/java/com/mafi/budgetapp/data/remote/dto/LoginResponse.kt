package com.mafi.budgetapp.data.remote.dto

data class LoginResponse (
    val token : String,
    val user : UserResponse
)
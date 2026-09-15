package com.mafi.budgetapp.ui.auth

import com.mafi.budgetapp.data.remote.dto.UserResponse

sealed class RegisterState {

    object Idle : RegisterState()

    object Loading : RegisterState()

    data class Success(val userResponse : UserResponse) : RegisterState()

    data class Error(val message: String) : RegisterState()
}
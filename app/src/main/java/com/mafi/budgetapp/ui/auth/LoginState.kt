package com.mafi.budgetapp.ui.auth

sealed class LoginState {

    object Idle : LoginState()

    object Loading : LoginState()

    data class Success(
        val token: String,
        val userName: String
    ) : LoginState()

    data class Error(
        val message: String
    ) : LoginState()
}


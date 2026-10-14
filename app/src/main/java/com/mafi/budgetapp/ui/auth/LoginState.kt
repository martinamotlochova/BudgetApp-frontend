package com.mafi.budgetapp.ui.auth

sealed class LoginState {

    data object Idle : LoginState()

    data object Loading : LoginState()

    data object  Success : LoginState()

    data class Error(val message: String) : LoginState()
}


package com.mafi.budgetapp.ui.session

sealed class AuthState {
    data object Loading: AuthState()
    data object Authenticated: AuthState()
    data object Unauthenticated: AuthState()
}
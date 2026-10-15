package com.mafi.budgetapp.ui.main

sealed class HomeUIState{
    data object Loading: HomeUIState()
    data class Success(val userName: String): HomeUIState()
    data class Error(val message: String): HomeUIState()
}
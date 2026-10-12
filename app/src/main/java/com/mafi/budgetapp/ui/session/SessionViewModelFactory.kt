package com.mafi.budgetapp.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mafi.budgetapp.data.local.TokenDataStore


class SessionViewModelFactory(private val tokenDataStore: TokenDataStore) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SessionViewModel(tokenDataStore) as T
    }
}
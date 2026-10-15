package com.mafi.budgetapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mafi.budgetapp.data.remote.AuthApiService
import com.mafi.budgetapp.ui.main.HomeViewModel

class HomeViewModelFactory(private val authApiService: AuthApiService): ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        @Suppress("UNCHECKED_CAST")
        return HomeViewModel(authApiService) as T
    }
}
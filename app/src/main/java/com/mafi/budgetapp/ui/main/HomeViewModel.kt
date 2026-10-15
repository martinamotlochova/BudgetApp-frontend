package com.mafi.budgetapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mafi.budgetapp.data.remote.AuthApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel (private val authApiService: AuthApiService): ViewModel(){

    private val mutableUiState = MutableStateFlow<HomeUIState>(HomeUIState.Loading)
    val uiState: StateFlow<HomeUIState> = mutableUiState

    init{
        loadUser()
    }

    fun loadUser(){
        mutableUiState.value = HomeUIState.Loading

        viewModelScope.launch {
            try{
                val response = authApiService.me()
                val user = response.body()

                if (response.isSuccessful && user != null){
                    mutableUiState.value = HomeUIState.Success(userName = user.name)
                } else {
                    mutableUiState.value = HomeUIState.Error("Could not load your profile")
                }
            } catch(e: Exception){
                mutableUiState.value = HomeUIState.Error("Connection failed.")
            }
        }
    }
}
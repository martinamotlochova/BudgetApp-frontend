package com.mafi.budgetapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mafi.budgetapp.data.local.TokenDataStore
import com.mafi.budgetapp.data.remote.RetrofitClient
import com.mafi.budgetapp.data.remote.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val mutableLoginState: MutableStateFlow<LoginState> =
        MutableStateFlow(LoginState.Idle)

    val loginState: StateFlow<LoginState> = mutableLoginState

    fun login(email: String, password: String) {
        mutableLoginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = password)
                val response = RetrofitClient.authApiService.login(request)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse != null) {
                        tokenDataStore.saveToken(loginResponse.token)

                        mutableLoginState.value = LoginState.Success(
                            token = loginResponse.token,
                            userName = loginResponse.user.name
                        )
                    } else {
                        mutableLoginState.value = LoginState.Error("Prázdna odpoveď zo servera.")
                    }
                } else {
                    mutableLoginState.value = LoginState.Error("Nesprávny email alebo heslo.")
                }
            } catch (e: Exception) {
                mutableLoginState.value = LoginState.Error("Chyba pripojenia: ${e.message}")
            }
        }
    }
}


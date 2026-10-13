package com.mafi.budgetapp.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mafi.budgetapp.data.local.TokenDataStore
import com.mafi.budgetapp.data.remote.AuthApiService
import com.mafi.budgetapp.data.remote.dto.LoginRequest
import com.mafi.budgetapp.data.remote.dto.LogoutRequest
import com.mafi.budgetapp.data.remote.dto.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.mafi.budgetapp.utils.isEmailValid
import com.mafi.budgetapp.utils.isPasswordValid
import kotlinx.coroutines.flow.first

class AuthViewModel(private val tokenDataStore: TokenDataStore, private val authApiService: AuthApiService) : ViewModel() {

    private val mutableLoginState: MutableStateFlow<LoginState> =
        MutableStateFlow(LoginState.Idle)

    private val mutableRegisterState: MutableStateFlow<RegisterState> =
        MutableStateFlow(RegisterState.Idle)

    val loginState: StateFlow<LoginState> = mutableLoginState

    val registerState: StateFlow<RegisterState> = mutableRegisterState

    fun login(email: String, password: String) {
        val emailValid = isEmailValid(email)
        val passwordValid = isPasswordValid(password)

        if (email.isEmpty()) {
            mutableLoginState.value = LoginState.Error("Email cannot be empty")
        } else if (!emailValid) {
            mutableLoginState.value = LoginState.Error("Invalid email format")
        } else if (password.isEmpty()) {
            mutableLoginState.value = LoginState.Error("Password cannot be empty")
        } else if (!passwordValid) {
            mutableLoginState.value = LoginState.Error(
                "Password must be at least 8 characters long, with an uppercase letter, a digit, and a special character"
            )
        } else {
            mutableLoginState.value = LoginState.Loading

            viewModelScope.launch {
                try {
                    val request = LoginRequest(email = email, password = password)
                    val response = authApiService.login(request)

                    if (response.isSuccessful) {
                        val loginResponse = response.body()

                        if (loginResponse != null) {
                            tokenDataStore.saveTokens(
                                accessToken = loginResponse.accessToken,
                                refreshToken = loginResponse.refreshToken
                            )
                            mutableLoginState.value = LoginState.Success(
                                token = loginResponse.accessToken,
                                userName = loginResponse.user.name
                            )
                        } else {
                            mutableLoginState.value =
                                LoginState.Error("Empty response from server.")
                        }
                    } else {
                        mutableLoginState.value = LoginState.Error("Incorrect email or password.")
                    }
                } catch (e: Exception) {
                    mutableLoginState.value = LoginState.Error("Connection failed: ${e.message}")
                }
            }
        }
    }


    fun register(email: String, name: String, password: String) {
        val nameValid = name.isNotEmpty()
        val emailValid = isEmailValid(email)
        val passwordValid = isPasswordValid(password)

        if (!nameValid) {
            mutableRegisterState.value = RegisterState.Error("Name cannot be empty")
        } else if (!emailValid) {
            mutableRegisterState.value = RegisterState.Error("Invalid email format")
        } else if (!passwordValid) {
            mutableRegisterState.value = RegisterState.Error(
                "Password must be at least 8 characters long, with an uppercase letter, a digit, and a special character"
            )
        } else {
            mutableRegisterState.value = RegisterState.Loading

            viewModelScope.launch {
                try {
                    val request = RegisterRequest(email = email, name = name, password = password)
                    val response = authApiService.register(request)

                    if (response.isSuccessful) {
                        val registerResponse = response.body()

                        if (registerResponse != null) {
                            mutableRegisterState.value =
                                RegisterState.Success(userResponse = registerResponse)
                        } else {
                            mutableRegisterState.value =
                                RegisterState.Error("Empty response from server.")
                        }
                    } else {
                        mutableRegisterState.value =
                            RegisterState.Error("Registration failed")
                    }
                } catch (e: Exception) {
                    mutableRegisterState.value =
                        RegisterState.Error("Connection failed: ${e.message}")
                }
            }
        }
    }

    fun logout(OnLoggedOut: () -> Unit){
        viewModelScope.launch {
           try{
               val refreshToken = tokenDataStore.getRefreshToken().first()
               if (!refreshToken.isNullOrBlank()){
                   authApiService.logout(LogoutRequest(refreshToken))
               }
           } catch (e: Exception){
               Log.w("Auth", "Logout request failed, logging out locally", e)
           } finally {
               tokenDataStore.clearTokens()
               OnLoggedOut()
           }
        }
    }
}


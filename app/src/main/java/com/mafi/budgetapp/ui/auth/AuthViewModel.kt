package com.mafi.budgetapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mafi.budgetapp.data.local.TokenDataStore
import com.mafi.budgetapp.data.remote.RetrofitClient
import com.mafi.budgetapp.data.remote.dto.LoginRequest
import com.mafi.budgetapp.data.remote.dto.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.mafi.budgetapp.utils.isEmailValid
import com.mafi.budgetapp.utils.isPasswordValid

class AuthViewModel(private val tokenDataStore: TokenDataStore) : ViewModel() {

    private val mutableLoginState: MutableStateFlow<LoginState> =
        MutableStateFlow(LoginState.Idle)

    private val mutableRegisterState: MutableStateFlow<RegisterState> =
        MutableStateFlow(RegisterState.Idle)

    val loginState: StateFlow<LoginState> = mutableLoginState

    val registerState: StateFlow<RegisterState> = mutableRegisterState

    fun login(email: String, password: String) {
        val emailValid = isEmailValid(email)
        val passwordValid = isPasswordValid(password)

        if (email.isNotEmpty() && password.isNotEmpty() && emailValid && passwordValid) {
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
                            mutableLoginState.value =
                                LoginState.Error("Prázdna odpoveď zo servera.")
                        }
                    } else {
                        mutableLoginState.value = LoginState.Error("Nesprávny email alebo heslo.")
                    }
                } catch (e: Exception) {
                    mutableLoginState.value = LoginState.Error("Chyba pripojenia: ${e.message}")
                }
            }
        } else {
            mutableLoginState.value = LoginState.Error(
                "Heslo musí mať aspoň 8 znakov, veľké písmeno, číslo a špeciálny znak"
            )
        }
    }


    fun register(email: String, name: String, password: String) {
        val nameValid = name.isNotEmpty()
        val emailValid = isEmailValid(email)
        val passwordValid = isPasswordValid(password)

        if (!nameValid) {
            mutableRegisterState.value = RegisterState.Error("Meno nesmie byť prázdne")
        } else if (!emailValid) {
            mutableRegisterState.value = RegisterState.Error("Neplatný formát emailu")
        } else if (!passwordValid) {
            mutableRegisterState.value = RegisterState.Error(
                "Heslo musí mať aspoň 8 znakov, veľké písmeno, číslo a špeciálny znak"
            )
        } else {
            mutableRegisterState.value = RegisterState.Loading

            viewModelScope.launch {
                try {
                    val request = RegisterRequest(email = email, name = name, password = password)
                    val response = RetrofitClient.authApiService.register(request)

                    if (response.isSuccessful) {
                        val registerResponse = response.body()

                        if (registerResponse != null) {
                            mutableRegisterState.value =
                                RegisterState.Success(userResponse = registerResponse)
                        } else {
                            mutableRegisterState.value =
                                RegisterState.Error("Prázdna odpoveď zo servera.")
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
}


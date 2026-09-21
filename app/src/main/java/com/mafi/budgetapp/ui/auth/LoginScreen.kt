package com.mafi.budgetapp.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mafi.budgetapp.utils.isEmailValid

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState.collectAsStateWithLifecycle()

    val isEmailFieldValid = email.isEmpty() || isEmailValid(email)

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = email,
            onValueChange = { newValue -> email = newValue },
            label = { Text("Email") },
            isError = !isEmailFieldValid,
            supportingText = {
                if (!isEmailFieldValid) {
                    Text("Invalid email format")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { newValue -> password = newValue },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { authViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log in")
        }

        when (loginState) {
            is LoginState.Loading -> {
                CircularProgressIndicator()
            }
            is LoginState.Error -> {
                val errorState = loginState as LoginState.Error
                Text(errorState.message)
            }
            is LoginState.Success -> {
                val successState = loginState as LoginState.Success
                Text("Prihlásená ako: ${successState.userName}")
            }
            is LoginState.Idle -> {
            }
        }
    }
}
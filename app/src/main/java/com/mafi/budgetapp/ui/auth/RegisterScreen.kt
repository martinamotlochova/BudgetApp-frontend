package com.mafi.budgetapp.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mafi.budgetapp.utils.isEmailValid
import com.mafi.budgetapp.utils.isPasswordValid

@Composable
fun RegisterScreen(authViewModel: AuthViewModel, onRegisterSuccess: () -> Unit) {

    var name by remember { mutableStateOf("")}
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isEmailFieldValid = email.isEmpty() || isEmailValid(email)
    val isPasswordFieldValid = password.isEmpty() || isPasswordValid(password)

    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = name,
            onValueChange = { newValue -> name = newValue },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

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
            isError = !isPasswordFieldValid,
            supportingText = {
                if (!isPasswordFieldValid) {
                    Text("Min 8 characters, uppercase letter, digit, special character")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { authViewModel.register(email, name, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        when (registerState) {
            is RegisterState.Loading -> {
                CircularProgressIndicator()
            }
            is RegisterState.Error -> {
                val errorState = registerState as RegisterState.Error
                Text(errorState.message)
            }
            is RegisterState.Success -> {
                Text("Registration completed!")
                LaunchedEffect(Unit) {
                    onRegisterSuccess()
                }
            }
            is RegisterState.Idle -> {
            }
        }
    }
}
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

@Composable
fun RegisterScreen(authViewModel: AuthViewModel) {

    var name by remember { mutableStateOf("")}
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { newValue -> password = newValue },
            label = { Text("Password") },
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
                val successState = registerState as RegisterState.Success
                Text("Registration completed!")
            }
            is RegisterState.Idle -> {
            }
        }
    }
}
package com.mafi.budgetapp.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions                 // NEW
import androidx.compose.foundation.text.KeyboardOptions                 // NEW
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
import androidx.compose.ui.focus.FocusDirection                         // NEW
import androidx.compose.ui.platform.LocalFocusManager                   // NEW
import androidx.compose.ui.text.input.ImeAction                         // NEW
import androidx.compose.ui.text.input.KeyboardCapitalization            // NEW
import androidx.compose.ui.text.input.KeyboardType                      // NEW
import androidx.compose.ui.text.input.PasswordVisualTransformation      // NEW
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mafi.budgetapp.ui.components.PasswordTextField
import com.mafi.budgetapp.utils.isEmailValid
import com.mafi.budgetapp.utils.isPasswordValid

@Composable
fun RegisterScreen(authViewModel: AuthViewModel, onRegisterSuccess: () -> Unit) {

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isEmailFieldValid = email.isEmpty() || isEmailValid(email)
    val isPasswordFieldValid = password.isEmpty() || isPasswordValid(password)

    val registerState by authViewModel.registerState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current                         // NEW

    Column(modifier = Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = name,
            onValueChange = { newValue -> name = newValue },
            label = { Text("Name") },
            singleLine = true,                                           // NEW
            keyboardOptions = KeyboardOptions(                           // NEW
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(                           // NEW
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
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
            singleLine = true,                                           // NEW
            keyboardOptions = KeyboardOptions(                           // NEW
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(                           // NEW
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        PasswordTextField(
            value = password,
            onValueChange = { newValue -> password = newValue },
            onDone = {
                focusManager.clearFocus()
                authViewModel.register(email, name, password)
            },
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

        when (val state = registerState) {                               // CHANGED
            is RegisterState.Loading -> {
                CircularProgressIndicator()
            }
            is RegisterState.Error -> {
                Text(state.message)                                      // CHANGED
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
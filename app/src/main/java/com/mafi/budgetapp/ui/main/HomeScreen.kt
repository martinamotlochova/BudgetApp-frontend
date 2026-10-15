package com.mafi.budgetapp.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    uiState: HomeUIState,
    onRetryClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState) {
            HomeUIState.Loading -> CircularProgressIndicator()

            is HomeUIState.Success -> Text(
                text = "Hello, ${uiState.userName}!",
                style = MaterialTheme.typography.headlineMedium
            )

            is HomeUIState.Error -> {
                Text(text = uiState.message)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetryClick) {
                    Text("Try again")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onLogoutClick) {
            Text("Log out")
        }
    }
}
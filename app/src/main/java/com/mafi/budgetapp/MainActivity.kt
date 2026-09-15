package com.mafi.budgetapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mafi.budgetapp.data.local.TokenDataStore
import com.mafi.budgetapp.ui.auth.AuthViewModel
import com.mafi.budgetapp.ui.auth.AuthViewModelFactory
import com.mafi.budgetapp.ui.auth.LoginScreen
import com.mafi.budgetapp.ui.theme.BudgetAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetAppTheme {
                val context = LocalContext.current
                val tokenDataStore = TokenDataStore(context)
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(tokenDataStore)
                )

                LoginScreen(authViewModel = authViewModel)
                }
            }
        }
    }


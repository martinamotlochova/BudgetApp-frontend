package com.mafi.budgetapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mafi.budgetapp.data.local.TokenDataStore
import com.mafi.budgetapp.ui.auth.AuthViewModel
import com.mafi.budgetapp.ui.auth.AuthViewModelFactory
import com.mafi.budgetapp.ui.auth.LoginScreen
import com.mafi.budgetapp.ui.auth.RegisterScreen

sealed class AppScreen(val route: String){
    data object Login: AppScreen("login")
    data object Register: AppScreen("register")
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
){
    val context = LocalContext.current
    val tokenDataStore = TokenDataStore(context)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(tokenDataStore)
    )
    NavHost(
        navController = navController,
        startDestination = AppScreen.Register.route
    ){
        composable (AppScreen.Register.route){
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                navController.navigate(AppScreen.Login.route)
            })
        }
        composable (AppScreen.Login.route){
            LoginScreen(authViewModel = authViewModel)
        }
    }
}
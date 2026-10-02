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
import androidx.navigation.compose.navigation
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
    data object Welcome: AppScreen("welcome")
    data object Login: AppScreen("login")
    data object Register: AppScreen("register")
    data object Home: AppScreen ("home")
}

object AppGraph {
    const val AUTH = "auth"
    const val MAIN = "main"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val tokenDataStore = TokenDataStore(context)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(tokenDataStore)
    )

    NavHost(navController = navController, startDestination = AppGraph.AUTH) {

        navigation(startDestination = AppScreen.Welcome.route, route = AppGraph.AUTH) {
            composable(AppScreen.Welcome.route) {
                // TODO: WelcomeScreen
            }
            composable(AppScreen.Register.route) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = { navController.navigate(AppScreen.Login.route) }
                )
            }
            composable(AppScreen.Login.route) {
                LoginScreen(authViewModel = authViewModel)
                // TODO: onLoginSuccess -> go to the main graph
            }
        }

        navigation(startDestination = AppScreen.Home.route, route = AppGraph.MAIN) {
            composable(AppScreen.Home.route) {
                // TODO: HomeScreen
            }
        }
    }
}
package com.mafi.budgetapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.mafi.budgetapp.BudgetApplication
import com.mafi.budgetapp.ui.auth.AuthViewModel
import com.mafi.budgetapp.ui.auth.AuthViewModelFactory
import com.mafi.budgetapp.ui.auth.LoginScreen
import com.mafi.budgetapp.ui.auth.RegisterScreen
import com.mafi.budgetapp.ui.auth.WelcomeScreen
import com.mafi.budgetapp.ui.main.HomeScreen
import com.mafi.budgetapp.ui.main.HomeViewModel
import com.mafi.budgetapp.ui.main.HomeViewModelFactory
import com.mafi.budgetapp.ui.session.AuthState
import com.mafi.budgetapp.ui.session.LoadingScreen
import com.mafi.budgetapp.ui.session.SessionViewModel
import com.mafi.budgetapp.ui.session.SessionViewModelFactory

sealed class AppScreen(val route: String) {
    data object Welcome : AppScreen("welcome")
    data object Loading : AppScreen("loading")
    data object Login : AppScreen("login")
    data object Register : AppScreen("register")
    data object Home : AppScreen("home")
}

object AppGraph {
    const val AUTH = "auth"
    const val MAIN = "main"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val container = (LocalContext.current.applicationContext as BudgetApplication).container

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(container.tokenDataStore, container.authApiService)
    )
    val sessionViewModel: SessionViewModel = viewModel(
        factory = SessionViewModelFactory(container.tokenDataStore)
    )
    val authState by sessionViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState == AuthState.Unauthenticated) {
            val currentGraph = navController.currentBackStackEntry?.destination?.parent?.route
            if (currentGraph == AppGraph.MAIN) {
                navController.navigate(AppGraph.AUTH) {
                    popUpTo(AppGraph.MAIN) { inclusive = true }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = AppScreen.Loading.route) {

        composable(AppScreen.Loading.route) {
            LoadingScreen(
                authState = authState,
                onAuthenticated = {
                    navController.navigate(AppGraph.MAIN) {
                        popUpTo(AppScreen.Loading.route) { inclusive = true }
                    }
                },
                onUnauthenticated = {
                    navController.navigate(AppGraph.AUTH) {
                        popUpTo(AppScreen.Loading.route) { inclusive = true }
                    }
                }
            )
        }

        navigation(startDestination = AppScreen.Welcome.route, route = AppGraph.AUTH) {
            composable(AppScreen.Welcome.route) {
                WelcomeScreen(
                    onLoginClick = { navController.navigate(AppScreen.Login.route) },
                    onRegisterClick = { navController.navigate(AppScreen.Register.route) }
                )
            }
            composable(AppScreen.Register.route) {
                RegisterScreen(
                    authViewModel = authViewModel,
                    onRegisterSuccess = { navController.navigate(AppScreen.Login.route) }
                )
            }
            composable(AppScreen.Login.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(AppGraph.MAIN) {
                            popUpTo(AppGraph.AUTH) { inclusive = true }
                        }
                    }
                )
            }
        }

        navigation(startDestination = AppScreen.Home.route, route = AppGraph.MAIN) {
            composable(AppScreen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(container.authApiService)
                )
                val homeUiState by homeViewModel.uiState.collectAsState()

                HomeScreen(
                    uiState = homeUiState,
                    onRetryClick = { homeViewModel.loadUser() },
                    onLogoutClick = { authViewModel.logout() }
                )
            }
        }
    }
}
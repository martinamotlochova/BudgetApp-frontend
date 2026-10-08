package com.mafi.budgetapp.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mafi.budgetapp.data.local.TokenDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class SessionViewModel(tokenDataStore: TokenDataStore) : ViewModel() {

    val authState: StateFlow<AuthState> = tokenDataStore.getRefreshToken()
        .map { token ->
            if (token.isNullOrBlank()) AuthState.Unauthenticated else AuthState.Authenticated
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Loading
        )
}
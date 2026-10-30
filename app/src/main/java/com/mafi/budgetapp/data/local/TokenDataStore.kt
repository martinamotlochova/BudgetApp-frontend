package com.mafi.budgetapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_preferences")

class TokenDataStore(
    private val context: Context,
    private val cryptoManager: CryptoManager                              // NEW
) {

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = cryptoManager.encrypt(accessToken)    // CHANGED
            prefs[REFRESH_TOKEN_KEY] = cryptoManager.encrypt(refreshToken)  // CHANGED
        }
    }

    fun getAccessToken(): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN_KEY]?.let { decryptOrNull(it) }             // CHANGED
        }

    fun getRefreshToken(): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY]?.let { decryptOrNull(it) }            // CHANGED
        }

    suspend fun clearTokens() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
    }

    // NEW: if decryption fails (old plain token, key lost after reinstall), act as "no token"
    private fun decryptOrNull(value: String): String? =
        try {
            cryptoManager.decrypt(value)
        } catch (e: Exception) {
            null
        }

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}
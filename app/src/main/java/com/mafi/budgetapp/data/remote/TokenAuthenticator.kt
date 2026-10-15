package com.mafi.budgetapp.data.remote

import com.mafi.budgetapp.data.local.TokenDataStore
import com.mafi.budgetapp.data.remote.dto.RefreshRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenDataStore: TokenDataStore,
    private val refreshApiService: AuthApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // A. Auth endpoints never trigger a refresh
        val path = response.request.url.encodedPath
        if (path.endsWith("/api/auth/login") ||
            path.endsWith("/api/auth/register") ||
            path.endsWith("/api/auth/refresh")
        ) return null

        // B. Already retried once -> give up (no infinite loop)
        if (responseCount(response) >= 2) return null

        // C. Only one refresh at a time
        synchronized(this) {
            val failedToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            val currentToken = runBlocking { tokenDataStore.getAccessToken().first() }

            // D. Someone else already refreshed -> just retry with the new token
            if (!currentToken.isNullOrBlank() && currentToken != failedToken) {
                return response.request.withBearer(currentToken)
            }

            val refreshToken = runBlocking { tokenDataStore.getRefreshToken().first() }
            if (refreshToken.isNullOrBlank()) return null

            return try {
                val refreshResponse = runBlocking {
                    refreshApiService.refresh(RefreshRequest(refreshToken))
                }
                val body = refreshResponse.body()

                when {
                    // E. Success -> save BOTH new tokens and retry
                    refreshResponse.isSuccessful && body != null -> {
                        runBlocking {
                            tokenDataStore.saveTokens(
                                body.accessToken,
                                body.refreshToken
                            )
                        }
                        response.request.withBearer(body.accessToken)
                    }
                    // F. Refresh token invalid/expired/revoked -> forced logout
                    refreshResponse.code() == 401 -> {
                        runBlocking { tokenDataStore.clearTokens() }
                        null
                    }
                    // G. Other server error -> keep tokens, just fail
                    else -> null
                }
            } catch (e: Exception) {
                // H. Network problem -> keep tokens, just fail
                null
            }
        }
    }

    private fun Request.withBearer(token: String): Request =
        newBuilder().header("Authorization", "Bearer $token").build()

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
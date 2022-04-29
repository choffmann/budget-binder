package de.hsfl.budgetBinder.data.client

import de.hsfl.budgetBinder.common.User
import io.ktor.client.plugins.auth.providers.*

interface ApiClient {
    // '/login'
    suspend fun login(username: String, password: String): BearerTokens

    suspend fun refreshToken(): BearerTokens

    // '/users/me'
    suspend fun getMyUser(): User

    // '/path'
    suspend fun path(): String
}
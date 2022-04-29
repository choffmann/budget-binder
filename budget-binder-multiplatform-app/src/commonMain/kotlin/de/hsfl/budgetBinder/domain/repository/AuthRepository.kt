package de.hsfl.budgetBinder.domain.repository

import io.ktor.client.plugins.auth.providers.*

interface AuthRepository {
    suspend fun authorize(username: String, password: String): BearerTokens
}
package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import io.ktor.client.plugins.auth.providers.*

class AuthRepositoryImplementation(
    private val client: Client
): AuthRepository {
    override suspend fun authorize(username: String, password: String): BearerTokens {
        return client.login(username, password)
    }
}
package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.domain.repository.AuthRepository

class AuthRepositoryImplementation(
    private val client: Client
) : AuthRepository {
    override suspend fun authorize(username: String, password: String) {
        return client.login(username, password)
    }
}
package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.common.*
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val client: Client
) : AuthRepository {
    override suspend fun authorize(email: String, password: String): APIResponse<AuthToken> {
        return client.login(email, password)
    }

    override suspend fun register(user: User.In): APIResponse<User> {
        return client.register(user)
    }

    override suspend fun logout(onAllDevice: Boolean) {
        return client.logout(onAllDevice)
    }
}
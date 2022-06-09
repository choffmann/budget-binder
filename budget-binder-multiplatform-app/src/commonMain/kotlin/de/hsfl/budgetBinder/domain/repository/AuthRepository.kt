package de.hsfl.budgetBinder.domain.repository

import de.hsfl.budgetBinder.common.*

interface AuthRepository {
    suspend fun authorize(email: String, password: String): APIResponse<AuthToken>

    suspend fun register(user: User.In): APIResponse<User>

    suspend fun logout(onAllDevice: Boolean)
}
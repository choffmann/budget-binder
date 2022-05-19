package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.domain.repository.UserRepository

class UserRepositoryImpl(
    private val client: Client
): UserRepository {
    override suspend fun getMyUser(): APIResponse<User> {
        return client.getMyUser()
    }
}
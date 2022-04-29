package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.domain.repository.UserRepository

class UserRepositoryImplementation(
    private val client: Client
): UserRepository {
    override suspend fun getMyUser(): User {
        return client.getMyUser()
    }

    override suspend fun getUserById(userId: Int): User {
        TODO("Not yet implemented")
    }

}
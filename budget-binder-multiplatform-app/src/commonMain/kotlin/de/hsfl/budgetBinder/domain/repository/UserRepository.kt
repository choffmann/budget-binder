package de.hsfl.budgetBinder.domain.repository

import de.hsfl.budgetBinder.common.User

interface UserRepository {
    suspend fun getMyUser(): User
    suspend fun getUserById(userId: Int): User
}
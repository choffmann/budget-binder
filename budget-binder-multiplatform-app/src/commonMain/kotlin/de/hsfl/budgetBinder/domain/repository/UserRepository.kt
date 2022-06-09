package de.hsfl.budgetBinder.domain.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.User

interface UserRepository {
    suspend fun getMyUser(): APIResponse<User>
    suspend fun changeMyUser(user: User.In): APIResponse<User>
    suspend fun removeMyUser(): APIResponse<User>
}
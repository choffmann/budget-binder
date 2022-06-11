package de.hsfl.budgetBinder.domain.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.User

interface UserRepository {
    /**
     * Get user information from logged in user
     * @author Cedrik Hoffmann
     */
    suspend fun getMyUser(): APIResponse<User>

    /**
     * Change the current logged in user
     * @param user User with changes
     * @author Cedrik Hoffmann
     */
    suspend fun changeMyUser(user: User.In): APIResponse<User>

    /**
     * Remove the user
     * @author Cedrik Hoffmann
     */
    suspend fun deleteMyUser(): APIResponse<User>
}
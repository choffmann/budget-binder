package de.hsfl.budgetBinder.domain.repository

import de.hsfl.budgetBinder.common.*

interface AuthRepository {
    /**
     * Authorize the user with given information
     * @param email email from user to login
     * @param password password from user to login
     * @author Cedrik Hoffmann
     */
    suspend fun authorize(email: String, password: String): APIResponse<AuthToken>

    /**
     * Register a user in the App
     * @param user User information to register
     * @author Cedrik Hoffmann
     */
    suspend fun register(user: User.In): APIResponse<User>

    /**
     * Logout the User on this or all Devices
     * @param onAllDevice logout only on current device (false) or on all devices (true)
     * @author Cedrik hoffmann
     */
    suspend fun logout(onAllDevice: Boolean): APIResponse<AuthToken>
}
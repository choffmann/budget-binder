package de.hsfl.budgetBinder.server.services.interfaces

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserPrincipal

interface UserService {
    fun findUserByEmailAndPassword(email: String, password: String): UserPrincipal?
    fun getUserPrincipalByIDAndTokenVersion(id: Int, tokenVersion: Int): UserPrincipal?
    fun findUserByID(id: Int): User?
    fun changeUser(userId: Int, userPut: User.Put): User
    fun logoutAllClients(userId: Int)
    fun insertNewUserOrNull(userIn: User.In): User?
    fun deleteUser(userId: Int): User
}

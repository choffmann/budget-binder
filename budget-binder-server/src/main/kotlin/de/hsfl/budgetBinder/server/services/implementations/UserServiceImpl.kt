package de.hsfl.budgetBinder.server.services.implementations

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.models.UserPrincipal
import de.hsfl.budgetBinder.server.models.Users
import de.hsfl.budgetBinder.server.services.interfaces.UserService
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserServiceImpl : UserService {
    override fun findUserByEmailAndPassword(email: String, password: String): UserPrincipal? = transaction {
        UserEntity.find { Users.email eq email }.firstOrNull()?.let { user ->
            if (BCrypt.checkpw(password, user.passwordHash)) user else null
        }
    }

    override fun getUserPrincipalByIDAndTokenVersion(id: Int, tokenVersion: Int): UserPrincipal? = transaction {
        UserEntity.findById(id)?.let {
            if (it.tokenVersion == tokenVersion) it else null
        }
    }

    override fun findUserByID(id: Int): User = transaction {
        UserEntity.findById(id)!!.toDto()
    }

    override fun changeUser(userId: Int, userPut: User.Patch): User = transaction {
        val user = UserEntity[userId]

        userPut.name?.let { user.name = it }
        userPut.firstName?.let { user.firstName = it }
        userPut.password?.let { user.passwordHash = BCrypt.hashpw(it, BCrypt.gensalt()) }

        user.toDto()
    }

    override fun logoutAllClients(userId: Int): Unit = transaction {
        UserEntity[userId].tokenVersion++
    }

    override fun insertNewUserOrNull(userIn: User.In): User? {
        return try {
            transaction {
                UserEntity.new {
                    firstName = userIn.firstName
                    name = userIn.name
                    email = userIn.email
                    passwordHash = BCrypt.hashpw(userIn.password, BCrypt.gensalt())
                }.toDto()
            }
        } catch (_: ExposedSQLException) {
            null
        }
    }

    override fun deleteUser(userId: Int): User = transaction {
        val user = UserEntity[userId]
        val userDto = user.toDto()
        user.entries.forEach { it.delete() }
        user.categories.forEach { it.delete() }
        user.delete()
        userDto
    }
}

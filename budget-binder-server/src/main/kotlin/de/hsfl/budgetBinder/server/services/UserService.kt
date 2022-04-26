package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.Roles
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.models.Users
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserService {
    fun getRandomUser() = transaction {
        UserEntity.all().toList().random()
    }

    fun findUserByEmailAndPassword(email: String, password: String): UserEntity? = transaction {
        UserEntity.find { Users.email eq email and Users.active eq Op.TRUE }.firstOrNull()?.let { user ->
            if (BCrypt.checkpw(password, user.passwordHash)) user else null
        }
    }

    fun getAllUsers(): List<UserEntity> = transaction {
        UserEntity.all().toList()
    }

    fun findUserByID(id: Int): UserEntity? = transaction {
        UserEntity.findById(id)
    }

    fun changeAdminUser(user: UserEntity, userAdminPut: User.AdminPut): UserEntity = transaction {
        if (userAdminPut.active != null)
            user.active = userAdminPut.active!!
        if (userAdminPut.role != null)
            user.role = Roles.fromDto(userAdminPut.role!!)
        user
    }

    fun changeUser(user: UserEntity, userPut: User.Put): UserEntity = transaction {
        if (userPut.name != null)
            user.name = userPut.name!!
        if (userPut.firstName != null)
            user.firstName = userPut.firstName!!
        if (userPut.password != null)
            user.passwordHash = BCrypt.hashpw(userPut.password, BCrypt.gensalt())
        if (userPut.active != null)
            user.active = userPut.active!!
        user
    }

    fun logoutAllClients(user: UserEntity): Unit = transaction {
        user.tokenVersion++
    }

    fun insertNewUser(userIn: User.In): UserEntity = transaction {
        UserEntity.new {
            firstName = userIn.firstName
            name = userIn.name
            email = userIn.email
            passwordHash = BCrypt.hashpw(userIn.password, BCrypt.gensalt())
        }
    }
}
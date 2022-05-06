package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.models.Users
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UserService {
    fun getRandomUser() = transaction {
        UserEntity.all().toList().random()
    }

    fun findUserByEmailAndPassword(email: String, password: String): UserEntity? = transaction {
        UserEntity.find { Users.email eq email }.firstOrNull()?.let { user ->
            if (BCrypt.checkpw(password, user.passwordHash)) user else null
        }
    }

    fun findUserByID(id: Int): UserEntity? = transaction {
        UserEntity.findById(id)
    }

    fun changeUser(user: UserEntity, userPut: User.Put): UserEntity = transaction {
        userPut.name?.let {
            user.name = it
        }
        userPut.firstName?.let {
            user.firstName = it
        }
        userPut.password?.let {
            user.passwordHash = BCrypt.hashpw(it, BCrypt.gensalt())
        }
        user
    }

    fun logoutAllClients(user: UserEntity): Unit = transaction {
        user.tokenVersion++
    }

    fun insertNewUserOrNull(userIn: User.In): UserEntity? {
        val userEntity = transaction {
            try {
                UserEntity.new {
                    firstName = userIn.firstName
                    name = userIn.name
                    email = userIn.email
                    passwordHash = BCrypt.hashpw(userIn.password, BCrypt.gensalt())
                }
            } catch (_: ExposedSQLException) {
                null
            }
        }

        return userEntity?.let {
            val category = transaction {
                CategoryEntity.new {
                    name = "default"
                    color = Category.Color.DEFAULT
                    image = Category.Image.DEFAULT
                    budget = 0.0f
                    user = it
                }
            }
            transaction {
                it.category = category.id
            }
            it
        }
    }


    fun deleteUser(user: UserEntity): Unit = transaction {
        user.entries.forEach { it.delete() }
        user.category = null
        user.categories.forEach { it.delete() }
        user.delete()
    }
}
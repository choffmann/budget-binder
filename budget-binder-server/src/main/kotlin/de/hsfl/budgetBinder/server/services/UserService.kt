package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.models.Users
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun getRandomUser() = transaction {
        UserEntity.all().toList().random()
    }

    fun findUserByEmailAndPassword(email: String, password: String): UserEntity? = transaction {
        UserEntity.find { Users.email eq email }.firstOrNull()?.let {
            user -> if (password == user.passwordHash) user else null
        }
    }

    fun findUserByID(id: Int): UserEntity? = transaction {
        UserEntity.findById(id)
    }
}
package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.server.models.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction

class UserService {
    fun getRandomUser() = transaction {
        UserEntity.all().toList().random()
    }
}
package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryServiceImpl : CategoryService {
    override fun getAll(userId: Int): List<Category> = transaction {
        UserEntity[userId].categories.map { it.toDto() }
    }

    override fun findByID(userId: Int, id: Int): Category? = transaction {
        UserEntity[userId].categories.firstOrNull { it.id.value == id }?.toDto()
    }
}

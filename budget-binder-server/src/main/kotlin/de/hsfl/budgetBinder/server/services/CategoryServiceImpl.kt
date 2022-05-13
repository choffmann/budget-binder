package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryServiceImpl : CategoryService {
    override fun getAllCategories(userId: Int): List<Category> = transaction {
        UserEntity[userId].categories.map { it.toDto() }
    }

    override fun findCategoryByID(userId: Int, id: Int): Category? = transaction {
        UserEntity[userId].categories.firstOrNull { it.id.value == id }?.toDto()
    }

    override fun insertCategoryForUser(userId: Int, category: Category.In): Category = transaction {
        CategoryEntity.new {
            name = category.name
            color = category.color
            image = category.image
            budget = category.budget
            user = UserEntity[userId]
        }.toDto()
    }

    override fun changeCategory(userId: Int, categoryId: Int, category: Category.Patch): Category = transaction {
        val categoryEntity = CategoryEntity[categoryId]
        category.name?.let { categoryEntity.name = it }
        category.color?.let { categoryEntity.color = it }
        category.image?.let { categoryEntity.image = it }
        category.budget?.let { categoryEntity.budget = it }
        categoryEntity.toDto()
    }

    override fun deleteCategory(categoryId: Int): Category = transaction {
        val categoryEntity = CategoryEntity[categoryId]
        val returnValue = categoryEntity.toDto()
        categoryEntity.delete()
        returnValue
    }
}

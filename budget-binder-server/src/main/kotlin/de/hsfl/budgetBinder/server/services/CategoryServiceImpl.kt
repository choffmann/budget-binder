package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CategoryServiceImpl : CategoryService {
    override fun getAllCategories(userId: Int): List<Category> = transaction {
        val user = UserEntity[userId]
        user.categories.filter { it.id != user.category }.map { it.toDto() }
    }

    override fun findCategoryByID(userId: Int, id: Int): Category? = transaction {
        val user = UserEntity[userId]
        user.categories.firstOrNull { it.id.value == id && it.id != user.category }?.toDto()
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
        var categoryEntity = CategoryEntity[categoryId]
        if (category.budget != null) {
            val oldEntity = categoryEntity
            categoryEntity = CategoryEntity.new {
                name = oldEntity.name
                color = oldEntity.color
                image = oldEntity.image
                budget = category.budget!!
                user = oldEntity.user
            }
            oldEntity.child = categoryEntity.id
            oldEntity.ended = LocalDateTime.now()
        }

        category.name?.let { categoryEntity.name = it }
        category.color?.let { categoryEntity.color = it }
        category.image?.let { categoryEntity.image = it }

        categoryEntity.toDto()
    }

    override fun deleteCategory(categoryId: Int): Category = transaction {
        val categoryEntity = CategoryEntity[categoryId]
        val returnValue = categoryEntity.toDto()
        categoryEntity.delete()
        returnValue
    }
}

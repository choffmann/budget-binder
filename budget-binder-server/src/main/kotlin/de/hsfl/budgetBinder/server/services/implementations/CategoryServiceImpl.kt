package de.hsfl.budgetBinder.server.services.implementations

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.repository.isCreatedAndEndedCorrectPeriod
import de.hsfl.budgetBinder.server.services.interfaces.CategoryService
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CategoryServiceImpl : CategoryService {
    override fun getCategoriesByPeriod(userId: Int, period: LocalDateTime?): List<Category> =
        transaction {
            val user = UserEntity[userId]

            val value = period?.let { period ->
                user.categories.filter {
                    it.id != user.category && isCreatedAndEndedCorrectPeriod(it.created, it.ended, period)
                }
            } ?: user.categories.filter { it.id != user.category }

            value.map { it.toDto() }
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

    private fun createOrChangeCategory(oldCategory: CategoryEntity, patchBudget: Float): CategoryEntity {
        val now = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        if (oldCategory.created > period || oldCategory.entries.empty() || oldCategory.entries.all { it.created > period }) {
            oldCategory.budget = patchBudget
            return oldCategory
        }
        val newCategory = CategoryEntity.new {
            name = oldCategory.name
            color = oldCategory.color
            image = oldCategory.image
            budget = patchBudget
            user = oldCategory.user
        }

        oldCategory.child = newCategory.id
        oldCategory.ended = LocalDateTime.now()

        val plusPeriod = period.plusMonths(1)
        oldCategory.entries.forEach {
            val entryPeriod = LocalDateTime.of(it.created.year, it.created.month.value, 1, 0, 0)
            var changeEntity = it
            if (it.repeat && entryPeriod != period) {
                changeEntity = changeEntity.createChild()

                changeEntity.category = newCategory

            } else {
                if (it.created > period && it.created < plusPeriod)
                    it.category = newCategory
            }
        }

        return newCategory
    }

    override fun changeCategory(userId: Int, categoryId: Int, categoryPatch: Category.Patch): Category? = transaction {
        var categoryEntity = CategoryEntity[categoryId]
        if (categoryEntity.ended != null) {
            return@transaction null
        }
        if (categoryPatch.budget != null) {
            categoryEntity = createOrChangeCategory(categoryEntity, categoryPatch.budget!!)
        }

        categoryPatch.name?.let { categoryEntity.name = it }
        categoryPatch.color?.let { categoryEntity.color = it }
        categoryPatch.image?.let { categoryEntity.image = it }

        categoryEntity.toDto()
    }

    override fun deleteCategory(categoryId: Int): Category = transaction {
        val categoryEntity = CategoryEntity[categoryId]
        categoryEntity.entries.forEach { it.category = CategoryEntity[categoryEntity.user.category!!] }
        val returnValue = categoryEntity.toDto()
        categoryEntity.delete()
        returnValue
    }
}

package de.hsfl.budgetBinder.server.services.implementations

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.Entries
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.utils.isCreatedAndEndedInPeriod
import de.hsfl.budgetBinder.server.services.interfaces.CategoryService
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class CategoryServiceImpl : CategoryService {
    override fun getCategoriesByPeriod(userId: Int, period: LocalDateTime?): List<Category> =
        transaction {
            val user = UserEntity[userId]

            val value = period?.let { period ->
                user.categories.filter {
                    isCreatedAndEndedInPeriod(it.created, it.ended, period)
                }
            } ?: user.categories

            value.map { it.toDto() }
        }

    override fun findCategoryByID(userId: Int, id: Int): Category? = transaction {
        UserEntity[userId].categories.firstOrNull { it.id.value == id }?.toDto()
    }

    override fun createCategory(userId: Int, category: Category.In): Category = transaction {
        CategoryEntity.new {
            name = category.name
            color = category.color
            image = category.image
            budget = category.budget
            user = UserEntity[userId]
        }.toDto()
    }

    private fun getEntriesForCategory(categoryEntity: CategoryEntity): SizedIterable<EntryEntity> = transaction {
        EntryEntity.find { Entries.category eq categoryEntity.id }
    }

    private fun isNewCategory(categoryEntity: CategoryEntity): Boolean {
        val now = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        val entries = getEntriesForCategory(categoryEntity)
        return categoryEntity.created > period || entries.empty() || entries.all { it.created > period }
    }

    private fun changeEntriesWithCategory(oldCategory: CategoryEntity, newCategory: CategoryEntity?) {
        val now = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        val plusPeriod = period.plusMonths(1)
        val entries = getEntriesForCategory(oldCategory)
        entries.forEach {
            val entryPeriod = LocalDateTime.of(it.created.year, it.created.month.value, 1, 0, 0)
            var changeEntity = it
            if (it.repeat && entryPeriod != period) {
                changeEntity = changeEntity.createChild()

                changeEntity.category = newCategory?.id

            } else {
                if (it.created > period && it.created < plusPeriod)
                    it.category = newCategory?.id
            }
        }
    }

    private fun createOrChangeCategory(oldCategory: CategoryEntity, patchBudget: Float): CategoryEntity {
        if (isNewCategory(oldCategory)) {
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

        changeEntriesWithCategory(oldCategory, newCategory)
        return newCategory
    }

    override fun changeCategory(userId: Int, categoryId: Int, categoryPatch: Category.Patch): Category? = transaction {
        var categoryEntity = CategoryEntity[categoryId]
        if (categoryEntity.ended != null) {
            return@transaction null
        }

        categoryPatch.budget?.let { categoryEntity = createOrChangeCategory(categoryEntity, it) }
        categoryPatch.name?.let { categoryEntity.name = it }
        categoryPatch.color?.let { categoryEntity.color = it }
        categoryPatch.image?.let { categoryEntity.image = it }

        categoryEntity.toDto()
    }

    override fun deleteCategory(categoryId: Int): Category? = transaction {
        val categoryEntity = CategoryEntity[categoryId]
        if (categoryEntity.ended != null) {
            return@transaction null
        }
        val returnValue = categoryEntity.toDto()
        if (isNewCategory(categoryEntity)) {
            val entries = getEntriesForCategory(categoryEntity)
            entries.forEach { it.category = null }
            categoryEntity.delete()
            return@transaction returnValue
        }

        categoryEntity.ended = LocalDateTime.now()
        changeEntriesWithCategory(categoryEntity, null)
        returnValue
    }
}

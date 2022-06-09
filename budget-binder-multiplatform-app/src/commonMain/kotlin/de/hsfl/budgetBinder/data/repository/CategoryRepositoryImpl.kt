package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val client: Client
): CategoryRepository {
    override suspend fun getAllCategories(): APIResponse<List<Category>> {
        return client.getAllCategories()
    }

    override suspend fun getAllCategories(period: String): APIResponse<Category> {
        return client.getAllCategories(period)
    }

    override suspend fun createNewCategory(category: Category.In): APIResponse<Category> {
        return client.createNewCategory(category)
    }

    override suspend fun getCategoryById(id: Int): APIResponse<Category> {
        return client.getCategoryById(id)
    }

    override suspend fun changeCategoryById(category: Category.In, id: Int): APIResponse<Category> {
        return client.changeCategoryById(category, id)
    }

    override suspend fun removeCategoryById(category: Category.In, id: Int): APIResponse<Category> {
        return client.removeCategoryById(category, id)
    }

    override suspend fun getEntriesFromCategory(id: Int): APIResponse<List<Category>> {
        return client.getEntriesFromCategory(id)
    }
}
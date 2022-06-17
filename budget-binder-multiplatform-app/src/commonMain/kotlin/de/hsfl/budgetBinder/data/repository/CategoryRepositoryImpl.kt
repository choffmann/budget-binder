package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.domain.repository.CategoryRepository

class CategoryRepositoryImpl(
    private val client: Client
): CategoryRepository {
    override suspend fun getAllCategories(): APIResponse<List<Category>> {
        return client.getAllCategories()
    }

    override suspend fun getAllCategories(period: String): APIResponse<List<Category>> {
        return client.getAllCategories(period)
    }

    override suspend fun createNewCategory(category: Category.In): APIResponse<Category> {
        return client.createNewCategory(category)
    }

    override suspend fun getCategoryById(id: Int): APIResponse<Category> {
        return client.getCategoryById(id)
    }

    override suspend fun changeCategoryById(category: Category.Patch, id: Int): APIResponse<Category> {
        return client.changeCategoryById(category, id)
    }

    override suspend fun deleteCategoryById(id: Int): APIResponse<Category> {
        return client.deleteCategoryById(id)
    }

    override suspend fun getEntriesFromCategory(id: Int?): APIResponse<List<Entry>> {
        return client.getEntriesFromCategory(id)
    }
}

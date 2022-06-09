package de.hsfl.budgetBinder.domain.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category

interface CategoryRepository {
    suspend fun getAllCategories(): APIResponse<List<Category>>
    suspend fun getAllCategories(period: String): APIResponse<Category>
    suspend fun createNewCategory(category: Category.In): APIResponse<Category>
    suspend fun getCategoryById(id: Int): APIResponse<Category>
    suspend fun changeCategoryById(category: Category.In, id: Int): APIResponse<Category>
    suspend fun removeCategoryById(id: Int): APIResponse<Category>
    suspend fun getEntriesFromCategory(id: Int): APIResponse<List<Category>>
}
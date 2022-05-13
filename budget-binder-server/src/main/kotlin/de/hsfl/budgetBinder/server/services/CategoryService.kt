package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.ErrorModel

interface CategoryService {
    fun getAllCategories(userId: Int): List<Category>
    fun findCategoryByID(userId: Int, id: Int): Category?
    fun insertCategoryForUser(userId: Int, category: Category.In): Category
    fun changeCategory(userId: Int, categoryId: Int, category: Category.Patch): Category
    fun deleteCategory(categoryId: Int): Category

    suspend fun getByIDOrErrorResponse(
        userId: Int,
        id: Int?,
        callback: suspend (category: Category) -> APIResponse<Category>
    ): APIResponse<Category> {
        return id?.let {
            findCategoryByID(userId, it)?.let { category ->
                callback(category)
            } ?: APIResponse(ErrorModel("Entry not found"))
        } ?: APIResponse(ErrorModel("path parameter is not a number"))
    }
}

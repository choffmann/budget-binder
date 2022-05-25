package de.hsfl.budgetBinder.server.services.interfaces

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.ErrorModel
import java.time.LocalDateTime

interface CategoryService {
    fun getCategoriesByPeriod(userId: Int, period: LocalDateTime?): List<Category>
    fun findCategoryByID(userId: Int, id: Int): Category?
    fun createCategory(userId: Int, category: Category.In): Category
    fun changeCategory(userId: Int, categoryId: Int, categoryPatch: Category.Patch): Category?
    fun deleteCategory(categoryId: Int): Category?

    suspend fun getByIDOrErrorResponse(
        userId: Int,
        id: Int?,
        callback: suspend (category: Category) -> APIResponse<Category>
    ): APIResponse<Category> {
        return id?.let {
            findCategoryByID(userId, it)?.let { category ->
                callback(category)
            } ?: APIResponse(ErrorModel("Category not found"))
        } ?: APIResponse(ErrorModel("path parameter is not a number"))
    }
}

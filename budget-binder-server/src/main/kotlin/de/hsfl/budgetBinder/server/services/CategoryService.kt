package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.ErrorModel

interface CategoryService {
    fun getAll(userId: Int): List<Category>

    fun findByID(userId: Int, id: Int): Category?

    suspend fun getByIDOrErrorResponse(
        userId: Int,
        id: Int?,
        callback: suspend (category: Category) -> APIResponse<Category>
    ): APIResponse<Category> {
        return id?.let {
            findByID(userId, it)?.let { category ->
                callback(category)
            } ?: APIResponse(ErrorModel("Entry not found"))
        } ?: APIResponse(ErrorModel("path parameter is not a number"))
    }
}

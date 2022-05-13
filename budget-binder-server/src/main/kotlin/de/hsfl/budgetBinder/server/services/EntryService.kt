package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel

interface EntryService {
    fun getAll(userId: Int): List<Entry>

    fun findByID(userId: Int, id: Int): Entry?

    suspend fun getByIDOrErrorResponse(
        userId: Int,
        id: Int?,
        callback: suspend (category: Entry) -> APIResponse<Entry>
    ): APIResponse<Entry> {
        return id?.let {
            findByID(userId, it)?.let { entry ->
                callback(entry)
            } ?: APIResponse(ErrorModel("Entry not found"))
        } ?: APIResponse(ErrorModel("path parameter is not a number"))
    }
}

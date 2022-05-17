package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel

interface EntryService {
    fun getAllEntries(userId: Int): List<Entry>
    fun findEntryByID(userId: Int, id: Int): Entry?
    fun insertEntryForUser(userId: Int, entry: Entry.In): Entry
    fun changeEntry(userId: Int, entryId: Int, entry: Entry.Patch): Entry
    fun deleteEntry(entryId: Int): Entry
    fun getAllEntriesForCategoryIdParam(userId: Int, categoryId: String?): APIResponse<List<Entry>>

    suspend fun getByIDOrErrorResponse(
        userId: Int,
        id: Int?,
        callback: suspend (category: Entry) -> APIResponse<Entry>
    ): APIResponse<Entry> {
        return id?.let {
            findEntryByID(userId, it)?.let { entry ->
                callback(entry)
            } ?: APIResponse(ErrorModel("Entry not found"))
        } ?: APIResponse(ErrorModel("path parameter is not a number"))
    }

}

package de.hsfl.budgetBinder.server.services.interfaces

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel
import java.time.LocalDateTime

interface EntryService {
    fun getEntriesByPeriod(userId: Int, period: LocalDateTime?): List<Entry>
    fun findEntryByID(userId: Int, id: Int): Entry?
    fun createEntry(userId: Int, entry: Entry.In): Entry
    fun changeEntry(userId: Int, entryId: Int, entry: Entry.Patch): Entry?
    fun deleteEntry(entryId: Int): Entry?
    fun getAllEntriesByPeriodForCategoryIdParam(
        userId: Int,
        period: LocalDateTime?,
        categoryId: String?
    ): APIResponse<List<Entry>>

    suspend fun getByIDOrErrorResponse(
        userId: Int,
        id: Int?,
        callback: suspend (category: Entry) -> APIResponse<Entry>
    ): APIResponse<Entry> {
        return id?.let {
            findEntryByID(userId, it)?.let { entry ->
                callback(entry)
            } ?: APIResponse(ErrorModel("Your entry was not found."))
        } ?: APIResponse(ErrorModel("The ID you provided is not a number."))
    }
}

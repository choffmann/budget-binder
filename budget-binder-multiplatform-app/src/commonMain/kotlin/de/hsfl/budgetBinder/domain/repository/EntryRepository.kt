package de.hsfl.budgetBinder.domain.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry

interface EntryRepository {
    suspend fun getAllEntries(): APIResponse<List<Entry>>
    suspend fun getAllEntries(period: String): APIResponse<List<Entry>>
    suspend fun createNewEntry(entry: Entry.In): APIResponse<Entry>
    suspend fun getEntryById(id: Int): APIResponse<Entry>
    suspend fun changeEntryById(entry: Entry.In, id: Int): APIResponse<Entry>
    suspend fun removeEntryById(id: Int): APIResponse<Entry>
}
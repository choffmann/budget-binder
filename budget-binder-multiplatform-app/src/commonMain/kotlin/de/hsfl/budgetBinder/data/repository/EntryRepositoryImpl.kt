package de.hsfl.budgetBinder.data.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.domain.repository.EntryRepository

class EntryRepositoryImpl(
    private val client: Client
): EntryRepository {
    override suspend fun getAllEntries(): APIResponse<List<Entry>> {
        return client.getAllEntries()
    }

    override suspend fun getAllEntries(period: String): APIResponse<List<Entry>> {
        return client.getAllEntries(period)
    }

    override suspend fun createNewEntry(entry: Entry.In): APIResponse<Entry> {
        return client.createNewEntry(entry)
    }

    override suspend fun getEntryById(id: Int): APIResponse<Entry> {
        return client.getEntryById(id)
    }

    override suspend fun changeEntryById(entry: Entry.In, id: Int): APIResponse<Entry> {
        return client.changeEntryById(entry, id)
    }

    override suspend fun removeEntryById(id: Int): APIResponse<Entry> {
        return client.removeEntryById(id)
    }
}
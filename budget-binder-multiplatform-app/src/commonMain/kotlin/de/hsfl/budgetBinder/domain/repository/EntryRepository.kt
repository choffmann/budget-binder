package de.hsfl.budgetBinder.domain.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry

interface EntryRepository {
    /**
     * Get all entries from current month
     * @author Cedrik Hoffmann
     */
    suspend fun getAllEntries(): APIResponse<List<Entry>>

    /**
     * Get all entries from a specific period
     * @param period Time period in format MM-YYYY (03-2022)
     * @author Cedrik Hoffmann
     */
    suspend fun getAllEntries(period: String): APIResponse<List<Entry>>

    /**
     * Create a new Entry
     * @param entry Entry to create
     * @author Cedrik Hoffmann
     */
    suspend fun createNewEntry(entry: Entry.In): APIResponse<Entry>

    /**
     * Get a Entry by ID
     * @param id ID from entry to get
     * @author cedrik Hoffmann
     */
    suspend fun getEntryById(id: Int): APIResponse<Entry>

    /**
     * Change a Entry by ID
     * @param entry Entry with changes
     * @param id ID from Entry to change
     * @author Cedrik Hoffmann
     */
    suspend fun changeEntryById(entry: Entry.In, id: Int): APIResponse<Entry>

    /**
     * Remove an entry by ID
     * @param id ID from entry to remove
     * @author Cedrik Hoffmann
     */
    suspend fun removeEntryById(id: Int): APIResponse<Entry>
}
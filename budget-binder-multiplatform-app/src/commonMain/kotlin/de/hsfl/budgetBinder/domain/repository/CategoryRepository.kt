package de.hsfl.budgetBinder.domain.repository

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry

interface CategoryRepository {
    /**
     * Get all categories from current month
     * @author Cedrik Hoffmann
     */
    suspend fun getAllCategories(): APIResponse<List<Category>>

    /**
     * Get all categories from a specific period
     * @param period Time period in format MM-YYYY (03-2022)
     * @author Cedrik Hoffmann
     */
    suspend fun getAllCategories(period: String): APIResponse<List<Category>>

    /**
     * Create a new Category
     * @param category Category to create
     * @author Cedrik Hoffmann
     */
    suspend fun createNewCategory(category: Category.In): APIResponse<Category>

    /**
     * Get a Category by ID
     * @param id ID from Category to get
     * @author cedrik Hoffmann
     */
    suspend fun getCategoryById(id: Int): APIResponse<Category>

    /**
     * Change a Category by ID
     * @param category Category with changes
     * @param id ID from Category to change
     * @author Cedrik Hoffmann
     */
    suspend fun changeCategoryById(category: Category.Patch, id: Int): APIResponse<Category>

    /**
     * Remove a Category by ID
     * @param id ID from category to remove
     * @author Cedrik Hoffmann
     */
    suspend fun deleteCategoryById(id: Int): APIResponse<Category>

    /**
     * Get all entries from a category of current month
     * @param id ID from Category to get all Entries from this
     * @author Cedrik Hoffmann
     */
    suspend fun getEntriesFromCategory(id: Int?): APIResponse<List<Entry>>

    /**
     * Get all entries from a category on period of time
     * @param id ID from Category to get all Entries from this
     * @param period Time period in format MM-YYYY (03-2022)
     * @author Cedrik Hoffmann
     */
    suspend fun getEntriesFromCategory(id: Int?, period: String): APIResponse<List<Entry>>
}

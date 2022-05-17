package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class EntryServiceImpl : EntryService {

    private fun getCategoryByID(userId: Int, categoryId: Int?): CategoryEntity = transaction {
        categoryId?.let { CategoryEntity.findById(it) } ?: CategoryEntity[UserEntity[userId].category!!]
    }

    override fun getAllEntries(userId: Int): List<Entry> = transaction {
        UserEntity[userId].entries.map { it.toDto() }
    }

    override fun findEntryByID(userId: Int, id: Int): Entry? = transaction {
        UserEntity[userId].entries.firstOrNull { it.id.value == id }?.toDto()
    }

    override fun insertEntryForUser(userId: Int, entry: Entry.In): Entry = transaction {
        EntryEntity.new {
            name = entry.name
            amount = entry.amount
            repeat = entry.repeat
            category = getCategoryByID(userId, entry.category_id)
            user = UserEntity[userId]
        }.toDto()
    }

    override fun changeEntry(userId: Int, entryId: Int, entry: Entry.Patch): Entry = transaction {
        var entryEntity = EntryEntity[entryId]

        if (entryEntity.repeat) {
            if (entry.repeat == false || entry.amount != null) {
                val oldEntity = entryEntity
                entryEntity = EntryEntity.new {
                    name = oldEntity.name
                    amount = oldEntity.amount
                    repeat = oldEntity.repeat
                    user = oldEntity.user
                }
                oldEntity.child = entryEntity.id
                oldEntity.ended = LocalDateTime.now()
            }
        }

        entry.name?.let { entryEntity.name = it }
        entry.amount?.let { entryEntity.amount = it }
        entry.repeat?.let { entryEntity.repeat = it }
        entry.category?.let { entryEntity.category = getCategoryByID(userId, it.id) }

        entryEntity.toDto()
    }

    override fun deleteEntry(entryId: Int): Entry = transaction {
        val entryEntity = EntryEntity[entryId]
        val returnValue = entryEntity.toDto()
        entryEntity.delete()
        returnValue
    }

    override fun getAllEntriesForCategoryIdParam(
        userId: Int,
        categoryId: String?
    ): APIResponse<List<Entry>> = transaction {
        val userEntity = UserEntity[userId]

        if (categoryId == "null") {
            APIResponse(data = CategoryEntity[userEntity.category!!].entries.map { it.toDto() }, success = true)
        } else {
            categoryId?.toIntOrNull()?.let { id ->
                CategoryEntity.findById(id)?.let { category ->
                    APIResponse(data = category.entries.map { it.toDto() }, success = true)
                } ?: APIResponse(ErrorModel("Category not found"))
            } ?: APIResponse(ErrorModel("path parameter is not a number"))
        }
    }
}

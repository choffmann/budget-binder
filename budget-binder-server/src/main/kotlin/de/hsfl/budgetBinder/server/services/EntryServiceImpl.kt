package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction

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
        val entryEntity = EntryEntity[entryId]
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
}

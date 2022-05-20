package de.hsfl.budgetBinder.server.services.implementations

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.models.CategoryEntity
import de.hsfl.budgetBinder.server.models.EntryEntity
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.repository.isCreatedAndEndedCorrectPeriod
import de.hsfl.budgetBinder.server.services.interfaces.EntryService
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class EntryServiceImpl : EntryService {

    private fun getCategoryByID(userId: Int, categoryId: Int?): CategoryEntity = transaction {
        categoryId?.let { CategoryEntity.findById(it) } ?: CategoryEntity[UserEntity[userId].category!!]
    }

    override fun getEntriesByPeriod(userId: Int, period: LocalDateTime?): List<Entry> = transaction {
        val user = UserEntity[userId]

        val value = period?.let {
            user.entries.filter {
                if (it.repeat)
                    isCreatedAndEndedCorrectPeriod(it.created, it.ended, period)
                else
                    it.created > period && it.created < period.plusMonths(1)
            }
        } ?: user.entries

        value.map { it.toDto() }
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

    private fun createOrChangeEntry(oldEntry: EntryEntity, repeat: Boolean?, amount: Float?): EntryEntity {
        val now = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)

        if (!oldEntry.repeat || (repeat != false && amount == null) || oldEntry.created > period) {
            return oldEntry
        }

        val newEntry = oldEntry.createChild()
        oldEntry.child = newEntry.id
        oldEntry.ended = now

        return newEntry
    }

    override fun changeEntry(userId: Int, entryId: Int, entry: Entry.Patch): Entry? = transaction {
        var entryEntity = EntryEntity[entryId]
        if (entryEntity.ended != null) {
            return@transaction null
        }

        val categoryEntity = entry.category?.let { getCategoryByID(userId, it.id) }

        if (categoryEntity?.ended != null) {
            return@transaction null
        }
        entryEntity = createOrChangeEntry(entryEntity, entry.repeat, entry.amount)

        entry.name?.let { entryEntity.name = it }
        entry.amount?.let { entryEntity.amount = it }
        entry.repeat?.let { entryEntity.repeat = it }
        categoryEntity?.let { entryEntity.category = it }

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

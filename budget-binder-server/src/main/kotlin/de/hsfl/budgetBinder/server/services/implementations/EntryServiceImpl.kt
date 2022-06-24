package de.hsfl.budgetBinder.server.services.implementations

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.models.*
import de.hsfl.budgetBinder.server.utils.isCreatedAndEndedInPeriod
import de.hsfl.budgetBinder.server.services.interfaces.EntryService
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class EntryServiceImpl : EntryService {

    private fun getCategoryByID(userId: Int, categoryId: Int?): CategoryEntity? = transaction {
        categoryId?.let {
            CategoryEntity.find { Categories.id eq categoryId and (Categories.user eq userId) }.firstOrNull()
        }
    }

    private fun filterEntriesByPeriod(it: EntryEntity, period: LocalDateTime): Boolean {
        return if (it.repeat)
            isCreatedAndEndedInPeriod(it.created, it.ended, period)
        else
            it.created > period && it.created < period.plusMonths(1)
    }

    override fun getEntriesByPeriod(userId: Int, period: LocalDateTime?): List<Entry> = transaction {
        val user = UserEntity[userId]

        val value = period?.let { period ->
            user.entries.filter { filterEntriesByPeriod(it, period) }
        } ?: user.entries

        value.map { it.toDto() }
    }

    override fun findEntryByID(userId: Int, id: Int): Entry? = transaction {
        UserEntity[userId].entries.firstOrNull { it.id.value == id }?.toDto()
    }

    override fun createEntry(userId: Int, entry: Entry.In): Entry = transaction {
        EntryEntity.new {
            name = entry.name
            amount = entry.amount
            repeat = entry.repeat
            category = getCategoryByID(userId, entry.category_id)?.id
            user = UserEntity[userId]
        }.toDto()
    }

    override fun changeEntry(userId: Int, entryId: Int, entry: Entry.Patch): Entry? = transaction {
        var entryEntity = EntryEntity[entryId]
        if (entryEntity.ended != null) {
            return@transaction null
        }

        if (entry.category == null) {
            if (!canEntryBeChanged(entryEntity, entry.repeat, entry.amount))
                entryEntity = entryEntity.createChild()
        } else {
            val categoryEntity = entry.category?.let { getCategoryByID(userId, it.id) }
            val maybeEntity = changeEntryWithCategoryChange(entryEntity, entry.repeat, entry.amount, categoryEntity)
            maybeEntity?.let {
                entryEntity = it
                it.category = categoryEntity?.id
            } ?: return@transaction null
        }

        entry.name?.let { entryEntity.name = it }
        entry.amount?.let { entryEntity.amount = it }
        entry.repeat?.let { entryEntity.repeat = it }
        entryEntity.toDto()
    }

    private fun changeEntryWithCategoryChange(
        oldEntry: EntryEntity,
        repeat: Boolean?,
        amount: Float?,
        categoryEntity: CategoryEntity?
    ): EntryEntity? {
        if (!canEntryBeChanged(oldEntry, repeat, amount)) {
            return oldEntry.createChild()
        }
        val now = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        val entryPeriod = LocalDateTime.of(oldEntry.created.year, oldEntry.created.month.value, 1, 0, 0)

        if (entryPeriod == period) {
            return oldEntry
        }

        if (oldEntry.repeat) {
            return oldEntry.createChild()
        }

        if (categoryEntity == null) {
            return oldEntry
        }

        val categoryPeriod = LocalDateTime.of(categoryEntity.created.year, categoryEntity.created.month.value, 1, 0, 0)

        if (categoryPeriod > entryPeriod) {
            return null
        }

        return oldEntry
    }

    private fun canEntryBeChanged(oldEntry: EntryEntity, repeat: Boolean?, amount: Float?): Boolean {
        val now = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)

        return !oldEntry.repeat || (repeat != false && amount == null) || oldEntry.created > period
    }

    override fun deleteEntry(entryId: Int): Entry? = transaction {
        val entryEntity = EntryEntity[entryId]
        if (entryEntity.ended != null) {
            return@transaction null
        }
        val returnValue = entryEntity.toDto()

        val now = LocalDateTime.now()
        val period = LocalDateTime.of(now.year, now.month.value, 1, 0, 0)
        if (entryEntity.repeat && entryEntity.created < period) {
            entryEntity.ended = now
        } else {
            EntryEntity.find { Entries.child eq entryEntity.id }.firstOrNull()?.let {
                it.child = null
            }
            entryEntity.delete()
        }

        returnValue
    }

    override fun getAllEntriesByPeriodForCategoryIdParam(
        userId: Int,
        period: LocalDateTime?,
        categoryId: String?
    ): APIResponse<List<Entry>> = transaction {
        val userEntity = UserEntity[userId]

        if (categoryId == "null") {
            val entries = EntryEntity.find { Entries.category eq null and (Entries.user eq userEntity.id) }
            APIResponse(data = entries.map { it.toDto() }, success = true)
        } else {
            categoryId?.toIntOrNull()?.let { id ->
                userEntity.categories.firstOrNull { it.id.value == id }?.let { categoryEntity ->
                    EntryEntity.find { Entries.category eq categoryEntity.id and (Entries.user eq userEntity.id) }
                        .let { entryEntities ->
                            period?.let {
                                entryEntities.filter { filterEntriesByPeriod(it, period) }
                            } ?: entryEntities
                        }.let { entries ->
                            APIResponse(data = entries.map { it.toDto() }, success = true)
                        }
                } ?: APIResponse(ErrorModel("Your category was not found."))
            } ?: APIResponse(ErrorModel("The ID you provided is not a number."))
        }
    }
}

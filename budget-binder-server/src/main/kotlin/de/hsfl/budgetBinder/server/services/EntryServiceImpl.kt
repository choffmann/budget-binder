package de.hsfl.budgetBinder.server.services

import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.server.models.UserEntity
import org.jetbrains.exposed.sql.transactions.transaction

class EntryServiceImpl : EntryService {
    override fun getAll(userId: Int): List<Entry> = transaction {
        UserEntity[userId].entries.map { it.toDto() }
    }

    override fun findByID(userId: Int, id: Int): Entry? = transaction {
        UserEntity[userId].entries.firstOrNull { it.id.value == id }?.toDto()
    }
}

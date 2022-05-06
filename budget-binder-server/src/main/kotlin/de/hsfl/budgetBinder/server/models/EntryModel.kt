package de.hsfl.budgetBinder.server.models

import de.hsfl.budgetBinder.common.Entry
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Entries : IntIdTable() {
    val name = varchar("name", 50)
    val amount = float("amount")
    val repeat = bool("repeat")
    val created = datetime("created").default(LocalDateTime.now())
    val ended = datetime("ended").nullable()

    val user = reference("user", Users)
    val category = reference("category", Categories)
}

class EntryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntryEntity>(Entries)

    var name by Entries.name
    var amount by Entries.amount
    var repeat by Entries.repeat
    var created by Entries.created
    var ended by Entries.ended

    val user by UserEntity referencedOn Entries.user
    val category by CategoryEntity referencedOn Entries.category

    fun toDto(): Entry {
        return Entry(name, amount, repeat, user.id.value, category.id.value)
    }
}
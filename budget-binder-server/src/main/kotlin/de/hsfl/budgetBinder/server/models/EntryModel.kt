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
    val repeat = bool("repeat").default(false)
    val created = datetime("created").clientDefault { LocalDateTime.now() }
    val ended = datetime("ended").nullable().default(null)

    val child = reference("child", Entries).nullable().default(null)

    val user = reference("user", Users)
    val category = reference("category", Categories).nullable().default(null)
}

class EntryIter(start: EntryEntity) : Iterator<EntryEntity> {
    private var curr = start
    override fun hasNext(): Boolean {
        return curr.child != null
    }

    override fun next(): EntryEntity {
        curr = EntryEntity[curr.child!!]
        return curr
    }
}

class EntryEntity(id: EntityID<Int>) : IntEntity(id), Iterable<EntryEntity> {
    companion object : IntEntityClass<EntryEntity>(Entries)

    var name by Entries.name
    var amount by Entries.amount
    var repeat by Entries.repeat
    var created by Entries.created
    var ended by Entries.ended

    var child by Entries.child

    var user by UserEntity referencedOn Entries.user
    var category by Entries.category


    fun toDto(): Entry {
        val lastChild = this.lastOrNull() ?: this

        return Entry(id.value, lastChild.name, amount, repeat, category?.value)
    }

    override fun iterator(): Iterator<EntryEntity> {
        return EntryIter(this)
    }

    fun createChild(): EntryEntity {
        val oldEntity = this
        val entryEntity = EntryEntity.new {
            this.name = oldEntity.name
            this.amount = oldEntity.amount
            this.repeat = oldEntity.repeat
            this.user = oldEntity.user
            this.category = oldEntity.category
        }
        this.child = entryEntity.id
        this.ended = LocalDateTime.now()
        return entryEntity
    }
}

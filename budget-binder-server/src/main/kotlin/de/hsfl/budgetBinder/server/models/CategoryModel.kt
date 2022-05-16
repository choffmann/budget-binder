package de.hsfl.budgetBinder.server.models

import de.hsfl.budgetBinder.common.Category
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Categories : IntIdTable() {
    val name = varchar("name", 50)
    val color = char("color", 6)
    val image = enumeration<Category.Image>("image")
    val budget = float("budget")
    val created = datetime("created").default(LocalDateTime.now())
    val ended = datetime("ended").nullable().default(null)

    val child = reference("child", Categories).nullable().default(null)

    val user = reference("user", Users)
}

class CategoryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CategoryEntity>(Categories)

    var name by Categories.name
    var color by Categories.color
    var image by Categories.image
    var budget by Categories.budget
    var created by Categories.created
    var ended by Categories.ended

    var child by Categories.child

    var user by UserEntity referencedOn Categories.user
    val entries by EntryEntity referrersOn Entries.category

    private fun next(): CategoryEntity? {
        return child?.let {
            CategoryEntity[it]
        }
    }

    private fun lastChild(): CategoryEntity {
        var lastChild = this
        while (true) {
            val child = lastChild.next() ?: break
            lastChild = child
        }
        return lastChild
    }

    fun toDto(): Category {
        val lastChild = lastChild()
        return Category(id.value, lastChild.name, lastChild.color, lastChild.image, budget)
    }
}
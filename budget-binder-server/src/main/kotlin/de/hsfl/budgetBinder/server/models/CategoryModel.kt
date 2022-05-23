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
    val created = datetime("created").clientDefault { LocalDateTime.now() }
    val ended = datetime("ended").nullable().default(null)

    val child = reference("child", Categories).nullable().default(null)

    val user = reference("user", Users)
}

class CategoryIter(start: CategoryEntity) : Iterator<CategoryEntity> {
    private var curr = start
    override fun hasNext(): Boolean {
        return curr.child != null
    }

    override fun next(): CategoryEntity {
        curr = CategoryEntity[curr.child!!]
        return curr
    }
}

class CategoryEntity(id: EntityID<Int>) : IntEntity(id), Iterable<CategoryEntity> {
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

    fun toDto(): Category {
        val lastChild = this.lastOrNull() ?: this
        return Category(id.value, lastChild.name, lastChild.color, lastChild.image, budget)
    }

    override fun iterator(): Iterator<CategoryEntity> {
        return CategoryIter(this)
    }
}

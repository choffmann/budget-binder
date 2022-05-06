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
    val color = enumeration<Category.Color>("color")
    val image = enumeration<Category.Image>("image")
    val budget = float("budget")
    val created = datetime("created").default(LocalDateTime.now())
    val ended = datetime("ended").nullable()

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

    var user by UserEntity referencedOn Categories.user
    val entries by EntryEntity referrersOn Entries.category

    fun toDto(): Category {
        return Category(id.value, name, color, image, budget)
    }
}
package de.hsfl.budgetBinder.server.models

import de.hsfl.budgetBinder.common.User
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val firstName = varchar("first_name", 50)
    val name = varchar("name", 50)
    val email = varchar("email", 50).uniqueIndex()
    val passwordHash = char("password_hash", 60)
    val tokenVersion = integer("token_version").default(1)
}

class UserEntity(id: EntityID<Int>) : IntEntity(id), UserPrincipal {
    companion object : IntEntityClass<UserEntity>(Users)

    var firstName by Users.firstName
    var name by Users.name
    var email by Users.email
    var passwordHash by Users.passwordHash
    var tokenVersion by Users.tokenVersion

    val categories by CategoryEntity referrersOn Categories.user
    val entries by EntryEntity referrersOn Entries.user

    fun toDto(): User {
        return User(id.value, firstName, name, email)
    }

    override fun getUserID(): Int {
        return id.value
    }

    override fun getUserTokenVersion(): Int {
        return tokenVersion
    }
}

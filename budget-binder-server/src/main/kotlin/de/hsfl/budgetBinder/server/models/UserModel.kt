package de.hsfl.budgetBinder.server.models

import de.hsfl.budgetBinder.common.User
import io.ktor.auth.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

enum class Roles {
    USER,
    ADMIN
}

object Users: IntIdTable() {
    val firstName = varchar("first_name", 50)
    val name = varchar("name", 50)
    val email = varchar("email", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 128)
    val role = enumeration<Roles>("role")
}

class UserEntity(id: EntityID<Int>) : IntEntity(id), Principal {
    companion object : IntEntityClass<UserEntity>(Users)

    var firstName by Users.firstName
    var name by Users.name
    var email by Users.email
    var passwordHash by Users.passwordHash
    var role by Users.role

    fun toDto(): User {
        return User(id.value, firstName, name, email)
    }
}
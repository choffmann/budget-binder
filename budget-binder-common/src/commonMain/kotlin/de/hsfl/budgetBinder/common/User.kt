package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val firstName: String,
    val name: String,
    val email: String,
    val role: Roles,
    val active: Boolean
) {
    enum class Roles {
        USER,
        ADMIN
    }

    @Serializable
    data class In(val firstName: String, val name: String, val email: String, val password: String)

    @Serializable
    data class Put(
        val firstName: String? = null,
        val name: String? = null,
        val active: Boolean? = null,
        val password: String? = null
    )

    @Serializable
    data class AdminPut(val active: Boolean? = null, val role: Roles? = null)
}

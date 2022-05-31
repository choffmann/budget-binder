package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val firstName: String,
    val name: String,
    val email: String
) {

    @Serializable
    data class In(val firstName: String, val name: String, val email: String, val password: String)

    @Serializable
    data class Patch(
        val firstName: String? = null,
        val name: String? = null,
        val password: String? = null
    )
}

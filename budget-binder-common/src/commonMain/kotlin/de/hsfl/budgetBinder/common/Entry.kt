package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    val id: Int,
    val name: String,
    val amount: Float,
    val repeat: Boolean,
    val category_id: Int?
) {
    @Serializable
    data class In(
        val name: String,
        val amount: Float,
        val repeat: Boolean,
        val category_id: Int? = null
    )

    @Serializable
    data class Category(val id: Int?)

    @Serializable
    data class Patch(
        val name: String? = null,
        val amount: Float? = null,
        val repeat: Boolean? = null,
        val category: Category? = null
    )
}

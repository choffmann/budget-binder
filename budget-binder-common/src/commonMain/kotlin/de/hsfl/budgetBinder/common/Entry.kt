package de.hsfl.budgetBinder.common

import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    val id: Int,
    val name: String,
    val amount: Float,
    val repeat: Boolean,
    val category_id: Int
)
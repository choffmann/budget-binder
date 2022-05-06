package de.hsfl.budgetBinder.common

data class Entry(
    val name: String,
    val amount: Float,
    val repeat: Boolean,
    val user_id: Int,
    val category_id: Int
)
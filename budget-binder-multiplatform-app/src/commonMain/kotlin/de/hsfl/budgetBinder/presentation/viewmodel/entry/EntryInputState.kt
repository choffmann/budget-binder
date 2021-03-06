package de.hsfl.budgetBinder.presentation.viewmodel.entry

data class EntryInputState(
    val name: String = "",
    val amount: Float = 0f,
    val repeat: Boolean = false,
    val categoryID: Int? = null,
    val amountSign: Boolean = false
)

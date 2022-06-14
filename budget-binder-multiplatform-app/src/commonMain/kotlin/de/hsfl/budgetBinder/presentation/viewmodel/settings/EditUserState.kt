package de.hsfl.budgetBinder.presentation.viewmodel.settings

data class EditUserState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val firstNameIsValid: Boolean = true,
    val lastNameIsValid: Boolean = true,
    val passwordIsValid: Boolean = true
)

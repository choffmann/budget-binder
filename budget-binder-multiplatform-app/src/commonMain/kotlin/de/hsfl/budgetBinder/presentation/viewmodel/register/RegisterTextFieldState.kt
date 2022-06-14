package de.hsfl.budgetBinder.presentation.viewmodel.register

data class RegisterTextFieldState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val emailValide: Boolean = true
)

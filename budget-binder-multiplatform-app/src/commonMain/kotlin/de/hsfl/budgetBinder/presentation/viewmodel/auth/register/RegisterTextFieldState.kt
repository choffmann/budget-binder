package de.hsfl.budgetBinder.presentation.viewmodel.auth.register

data class RegisterTextFieldState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmedPassword: String = "",
    val emailValid: Boolean = true,
    val confirmedPasswordValid: Boolean = true
)

package de.hsfl.budgetBinder.presentation.viewmodel.auth.login

data class LoginTextFieldState(
    val email: String = "",
    val password: String = "",
    val serverAddress: String = "",
    val emailValid: Boolean = true
)

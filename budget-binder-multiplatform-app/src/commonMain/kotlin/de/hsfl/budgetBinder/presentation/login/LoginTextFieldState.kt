package de.hsfl.budgetBinder.presentation.login

data class LoginTextFieldState(
    val email: String = "root@budget-binder.com",
    val password: String = "budget-binder",
    val serverAddress: String = "",
    val emailValid: Boolean = true
    //val email: String = "",
    //val password: String = ""
)

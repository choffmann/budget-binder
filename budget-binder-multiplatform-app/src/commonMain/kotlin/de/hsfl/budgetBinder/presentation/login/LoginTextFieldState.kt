package de.hsfl.budgetBinder.presentation.login

data class LoginTextFieldState(
    val email: String = "root@budget-binder.com",
    val password: String = "budget-binder",
    val emailValide: Boolean = true
    //val email: String = "",
    //val password: String = ""
)

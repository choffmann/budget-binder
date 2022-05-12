package de.hsfl.budgetBinder.presentation.component.login

interface LoginView {
    fun onLoginSuccess()
    fun login(username: String, password: String)

    sealed class Output {
        object LoggedIn : Output()
        object Loading : Output()
        data class Error(val error: String) : Output()
    }
}
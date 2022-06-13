package de.hsfl.budgetBinder.presentation.login

sealed class LoginEvent {
    data class EnteredEmail(val value: String): LoginEvent()
    data class EnteredPassword(val value: String): LoginEvent()
    object OnLogin: LoginEvent()
    object OnChangeToRegister: LoginEvent()
}
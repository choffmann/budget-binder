package de.hsfl.budgetBinder.presentation.register

sealed class RegisterEvent {
    data class EnteredFirstname(val value: String): RegisterEvent()
    data class EnteredLastname(val value: String): RegisterEvent()
    data class EnteredEmail(val value: String): RegisterEvent()
    data class EnteredPassword(val value: String): RegisterEvent()
    object OnRegister: RegisterEvent()
    object OnChangeToLogin: RegisterEvent()
}
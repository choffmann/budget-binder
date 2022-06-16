package de.hsfl.budgetBinder.presentation.viewmodel.register

sealed class RegisterEvent {
    data class EnteredFirstname(val value: String): RegisterEvent()
    data class EnteredLastname(val value: String): RegisterEvent()
    data class EnteredEmail(val value: String): RegisterEvent()
    data class EnteredPassword(val value: String): RegisterEvent()
    data class EnteredConfirmedPassword(val value: String): RegisterEvent()
    object OnRegister: RegisterEvent()
    object OnLoginScreen: RegisterEvent()
}

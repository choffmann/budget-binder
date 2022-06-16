package de.hsfl.budgetBinder.presentation.viewmodel.settings

sealed class EditUserEvent {
    data class EnteredFirstName(val value: String): EditUserEvent()
    data class EnteredLastName(val value: String): EditUserEvent()
    data class EnteredPassword(val value: String): EditUserEvent()
    data class EnteredConfirmedPassword(val value: String): EditUserEvent()
    object OnUpdate: EditUserEvent()
    object OnGoBack: EditUserEvent()
}

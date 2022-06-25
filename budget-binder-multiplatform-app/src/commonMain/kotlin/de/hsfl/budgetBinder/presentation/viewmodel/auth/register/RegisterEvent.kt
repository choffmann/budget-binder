package de.hsfl.budgetBinder.presentation.viewmodel.auth.register

import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.auth.login.LoginEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditEvent

sealed class RegisterEvent {
    data class LifeCycle(val value: LifecycleEvent): RegisterEvent()
    data class EnteredFirstname(val value: String): RegisterEvent()
    data class EnteredLastname(val value: String): RegisterEvent()
    data class EnteredEmail(val value: String): RegisterEvent()
    data class EnteredPassword(val value: String): RegisterEvent()
    data class EnteredConfirmedPassword(val value: String): RegisterEvent()
    data class EnteredServerUrl(val value: String) : RegisterEvent()
    object OnServerUrlDialogConfirm : RegisterEvent()
    object OnServerUrlDialogDismiss : RegisterEvent()
    object OnRegister: RegisterEvent()
    object OnLoginScreen: RegisterEvent()
}

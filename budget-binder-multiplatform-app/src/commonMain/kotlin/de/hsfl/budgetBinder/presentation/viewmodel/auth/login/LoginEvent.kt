package de.hsfl.budgetBinder.presentation.viewmodel.auth.login

import de.hsfl.budgetBinder.presentation.event.LifecycleEvent

sealed class LoginEvent {
    data class EnteredEmail(val value: String) : LoginEvent()
    data class EnteredPassword(val value: String) : LoginEvent()
    data class EnteredServerUrl(val value: String) : LoginEvent()
    data class LifeCycle(val value: LifecycleEvent): LoginEvent()
    object OnLogin : LoginEvent()
    object OnServerUrlDialogConfirm : LoginEvent()
    object OnServerUrlDialogDismiss : LoginEvent()
    object OnRegisterScreen : LoginEvent()
}

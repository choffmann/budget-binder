package de.hsfl.budgetBinder.presentation.viewmodel.settings

sealed class SettingsEvent {
    data class EnteredFirstName(val value: String): SettingsEvent()
    data class EnteredLastName(val value: String): SettingsEvent()
    data class EnteredPassword(val value: String): SettingsEvent()
    object OnChangeToSettingsUserEdit: SettingsEvent()
    object OnChangeToSettingsServerUrlEdit: SettingsEvent()
    object OnLogoutAllDevices: SettingsEvent()
    object OnDeleteUser: SettingsEvent()
    object OnDeleteDialogConfirm: SettingsEvent()
    object OnDeleteDialogDismiss: SettingsEvent()
}

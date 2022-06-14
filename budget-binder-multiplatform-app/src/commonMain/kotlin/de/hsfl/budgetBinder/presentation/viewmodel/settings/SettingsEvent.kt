package de.hsfl.budgetBinder.presentation.viewmodel.settings

sealed class SettingsEvent {
    object OnChangeToSettingsUserEdit: SettingsEvent()
    object OnChangeToSettingsServerUrlEdit: SettingsEvent()
    object OnLogoutAllDevices: SettingsEvent()
    object OnDeleteUser: SettingsEvent()
    object OnDeleteDialogConfirm: SettingsEvent()
    object OnDeleteDialogDismiss: SettingsEvent()
}

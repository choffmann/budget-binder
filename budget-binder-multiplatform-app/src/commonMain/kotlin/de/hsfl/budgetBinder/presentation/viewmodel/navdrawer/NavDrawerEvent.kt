package de.hsfl.budgetBinder.presentation.viewmodel.navdrawer

sealed class NavDrawerEvent {
    object OnChangeServerUrl: NavDrawerEvent()
    object OnToggleDarkMode: NavDrawerEvent()
    object OnDashboard: NavDrawerEvent()
    object OnCreateEntry: NavDrawerEvent()
    object OnCategory: NavDrawerEvent()
    object OnSettings: NavDrawerEvent()
    object OnLogout: NavDrawerEvent()
}

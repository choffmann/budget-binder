package de.hsfl.budgetBinder.presentation.viewmodel.navdrawer

sealed class NavDrawerEvent {
    object OnDashboard: NavDrawerEvent()
    object OnCreateEntry: NavDrawerEvent()
    object OnCategory: NavDrawerEvent()
    object OnSettings: NavDrawerEvent()
    object OnLogout: NavDrawerEvent()
}

package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

sealed class DashboardEvent {
    object OnPrevCategory : DashboardEvent()
    object OnNextCategory : DashboardEvent()
    data class OnEntry(val id: Int) : DashboardEvent()
    object OnEntryCreate : DashboardEvent()
}

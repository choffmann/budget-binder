package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

sealed class DashboardEvent {
    object OnCategoryChanged : DashboardEvent()
    data class OnEntry(val id: Int) : DashboardEvent()
    object OnCategoryCreate : DashboardEvent()
}

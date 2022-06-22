package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

import de.hsfl.budgetBinder.presentation.event.LifecycleEvent

sealed class DashboardEvent {
    data class LifeCycle(val value: LifecycleEvent): DashboardEvent()
    object OnPrevCategory : DashboardEvent()
    object OnNextCategory : DashboardEvent()
    object OnRefresh: DashboardEvent()
    object OnLoadMore: DashboardEvent()
    data class OnEntry(val id: Int) : DashboardEvent()
    data class OnEntryDelete(val id: Int): DashboardEvent()
    object OnEntryCreate : DashboardEvent()
}

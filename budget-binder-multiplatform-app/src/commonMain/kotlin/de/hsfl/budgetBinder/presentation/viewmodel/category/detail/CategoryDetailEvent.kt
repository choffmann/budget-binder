package de.hsfl.budgetBinder.presentation.viewmodel.category.detail

import de.hsfl.budgetBinder.presentation.event.LifecycleEvent

sealed class CategoryDetailEvent {
    object OnEdit: CategoryDetailEvent()
    object OnDelete: CategoryDetailEvent()
    object OnBack: CategoryDetailEvent()
    object OnRefresh: CategoryDetailEvent()
    data class LifeCycle(val value: LifecycleEvent): CategoryDetailEvent()
    data class OnEntry(val id: Int): CategoryDetailEvent()
}

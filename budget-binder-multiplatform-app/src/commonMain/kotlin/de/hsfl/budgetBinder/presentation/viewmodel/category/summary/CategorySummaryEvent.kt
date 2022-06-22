package de.hsfl.budgetBinder.presentation.viewmodel.category.summary

import de.hsfl.budgetBinder.presentation.event.LifecycleEvent

sealed class CategorySummaryEvent {
    data class LifeCycle(val value: LifecycleEvent): CategorySummaryEvent()
    data class OnCategory(val id: Int): CategorySummaryEvent()
    object OnCategoryCreate: CategorySummaryEvent()
    object OnRefresh: CategorySummaryEvent()
    // TODO: Maybe delete?
}

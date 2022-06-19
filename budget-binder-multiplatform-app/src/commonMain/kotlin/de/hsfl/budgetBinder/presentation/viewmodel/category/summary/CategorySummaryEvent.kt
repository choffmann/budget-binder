package de.hsfl.budgetBinder.presentation.viewmodel.category.summary

sealed class CategorySummaryEvent {
    data class OnCategory(val id: Int): CategorySummaryEvent()
    object OnCategoryCreate: CategorySummaryEvent()
    object OnRefresh: CategorySummaryEvent()
    // TODO: Maybe delete?
}

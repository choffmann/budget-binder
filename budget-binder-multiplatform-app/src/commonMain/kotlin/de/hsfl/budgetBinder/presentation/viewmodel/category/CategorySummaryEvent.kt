package de.hsfl.budgetBinder.presentation.viewmodel.category

sealed class CategorySummaryEvent {
    data class OnCategory(val id: Int): CategorySummaryEvent()
    object OnCategoryCreate: CategorySummaryEvent()
}

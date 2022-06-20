package de.hsfl.budgetBinder.presentation.viewmodel.category.detail

sealed class CategoryDetailEvent {
    object OnEdit: CategoryDetailEvent()
    object OnDelete: CategoryDetailEvent()
    object OnBack: CategoryDetailEvent()
    data class OnEntry(val id: Int): CategoryDetailEvent()
}

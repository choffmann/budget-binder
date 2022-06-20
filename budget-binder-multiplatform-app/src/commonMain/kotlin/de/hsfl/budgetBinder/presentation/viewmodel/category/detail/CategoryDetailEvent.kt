package de.hsfl.budgetBinder.presentation.viewmodel.category.detail

sealed class CategoryDetailEvent {
    object OnEdit: CategoryDetailEvent()
    object OnDelete: CategoryDetailEvent()
    object OnBack: CategoryDetailEvent()
    object OnRefresh: CategoryDetailEvent()
    object OnLaunch: CategoryDetailEvent()
    object OnDispose: CategoryDetailEvent()
    data class OnEntry(val id: Int): CategoryDetailEvent()
}

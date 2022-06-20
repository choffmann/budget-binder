package de.hsfl.budgetBinder.presentation.viewmodel.category.edit

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailEvent

sealed class CategoryEditEvent {
    data class EnteredCategoryName(val value: String): CategoryEditEvent()
    data class EnteredCategoryColor(val value: String): CategoryEditEvent()
    data class EnteredCategoryImage(val value: Category.Image): CategoryEditEvent()
    data class EnteredCategoryBudget(val value: Float): CategoryEditEvent()
    data class LifeCycle(val value: LifecycleEvent): CategoryEditEvent()
    object OnSave: CategoryEditEvent()
    object OnCancel: CategoryEditEvent()
    object OnDelete: CategoryEditEvent()
}

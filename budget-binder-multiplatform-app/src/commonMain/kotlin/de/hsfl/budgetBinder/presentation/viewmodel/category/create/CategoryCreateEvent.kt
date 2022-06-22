package de.hsfl.budgetBinder.presentation.viewmodel.category.create

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent

sealed class CategoryCreateEvent {
    data class EnteredCategoryName(val value: String): CategoryCreateEvent()
    data class EnteredCategoryColor(val value: String): CategoryCreateEvent()
    data class EnteredCategoryImage(val value: Category.Image): CategoryCreateEvent()
    data class EnteredCategoryBudget(val value: Float): CategoryCreateEvent()
    data class LifeCycle(val value: LifecycleEvent): CategoryCreateEvent()
    object OnSave: CategoryCreateEvent()
    object OnCancel: CategoryCreateEvent()
}

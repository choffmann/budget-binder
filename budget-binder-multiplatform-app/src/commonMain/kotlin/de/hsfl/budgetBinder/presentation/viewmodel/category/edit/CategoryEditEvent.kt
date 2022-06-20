package de.hsfl.budgetBinder.presentation.viewmodel.category.edit

import de.hsfl.budgetBinder.presentation.event.LifecycleEvent

sealed class CategoryEditEvent {
    data class LifeCycle(val value: LifecycleEvent): CategoryEditEvent()
    object OnSave: CategoryEditEvent()
    object OnDelete: CategoryEditEvent()
}

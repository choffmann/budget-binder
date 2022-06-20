package de.hsfl.budgetBinder.presentation.viewmodel.category.detail

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.domain.usecase.CategoriesUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoryDetailViewModel(
    categoriesUseCases: CategoriesUseCases,
    scope: CoroutineScope
) : CategoryViewModel(
    _categoriesUseCases = categoriesUseCases,
    _scope = scope
) {
    private val _categoryState =
        MutableStateFlow(Category(id = -1, name = "0", color = "111111", image = Category.Image.DEFAULT, budget = 0f))
    val categoryState: StateFlow<Category> = _categoryState

    fun onEvent(event: CategoryDetailEvent) {
        when (event) {
            is CategoryDetailEvent.OnEdit -> RouterFlow.navigateTo(Screen.Category.Edit)
            is CategoryDetailEvent.OnDelete -> super.delete(
                id = categoryState.value.id,
                onSuccess = { RouterFlow.navigateTo(Screen.Category.Summary) })
            is CategoryDetailEvent.OnBack -> RouterFlow.navigateTo(Screen.Category.Summary)
            is CategoryDetailEvent.OnEntry -> { /* TODO: Navigate to Entry detail */
            }
        }
    }
}

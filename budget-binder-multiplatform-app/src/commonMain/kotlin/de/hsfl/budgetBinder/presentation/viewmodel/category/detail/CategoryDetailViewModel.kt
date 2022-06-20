package de.hsfl.budgetBinder.presentation.viewmodel.category.detail

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
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

    private val _entryList = MutableStateFlow<List<Entry>>(emptyList())
    val entryList: StateFlow<List<Entry>> = _entryList

    private val currentCategoryId: Int = when (RouterFlow.state.value) {
        is Screen.Category.Detail -> (RouterFlow.state.value as Screen.Category.Detail).id
        else -> -1
    }

    init {
        getCategoryByCurrentId()
        setEntryList()
    }

    fun onEvent(event: CategoryDetailEvent) {
        when (event) {
            is CategoryDetailEvent.OnEdit -> RouterFlow.navigateTo(Screen.Category.Edit)
            is CategoryDetailEvent.OnDelete -> super.delete(
                id = currentCategoryId,
                onSuccess = { RouterFlow.navigateTo(Screen.Category.Summary) })
            is CategoryDetailEvent.OnBack -> RouterFlow.navigateTo(Screen.Category.Summary)
            is CategoryDetailEvent.OnRefresh -> getCategoryByCurrentId()
            is CategoryDetailEvent.OnEntry -> { /* TODO: Navigate to Entry detail */
            }
        }
    }

    private fun getCategoryByCurrentId() {
        if (currentCategoryId != -1) {
            super.getById(id = currentCategoryId, onSuccess = { _categoryState.value = it })
        }
    }

    private fun setEntryList() {
        if (currentCategoryId != -1) {
            super.entries(id = currentCategoryId, onSuccess = { _entryList.value = it })
        }
    }
}

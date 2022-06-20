package de.hsfl.budgetBinder.presentation.viewmodel.category.detail

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.CategoriesUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryDetailViewModel(
    categoriesUseCases: CategoriesUseCases,
    private val scope: CoroutineScope,
    private val routerFlow: RouterFlow
) : CategoryViewModel(
    _categoriesUseCases = categoriesUseCases,
    _scope = scope,
    _routerFlow = routerFlow
) {
    private val _categoryState =
        MutableStateFlow(Category(id = -1, name = "0", color = "111111", image = Category.Image.DEFAULT, budget = 0f))
    val categoryState: StateFlow<Category> = _categoryState

    private val _entryList = MutableStateFlow<List<Entry>>(emptyList())
    val entryList: StateFlow<List<Entry>> = _entryList
    private var currentCategoryId = -1

    fun onEvent(event: CategoryDetailEvent) {
        when (event) {
            is CategoryDetailEvent.OnEdit -> routerFlow.navigateTo(Screen.Category.Edit)
            is CategoryDetailEvent.OnDelete -> super.delete(
                id = currentCategoryId,
                onSuccess = { routerFlow.navigateTo(Screen.Category.Summary) })
            is CategoryDetailEvent.OnBack -> routerFlow.navigateTo(Screen.Category.Summary)
            is CategoryDetailEvent.OnRefresh -> initStateFlows()
            is CategoryDetailEvent.OnEntry -> { /* TODO: Navigate to Entry detail */
            }
            is CategoryDetailEvent.OnLaunch -> initStateFlows()
        }
    }

    private fun initStateFlows() {
        updateCurrentCategoryId()
        getCategoryByCurrentId()
        setEntryList()
    }

    private fun getCategoryByCurrentId() {
        super.getById(id = currentCategoryId, onSuccess = { _categoryState.value = it })
    }

    private fun setEntryList() {
        super.entries(id = currentCategoryId, onSuccess = { _entryList.value = it })

    }

    private fun updateCurrentCategoryId() {
        currentCategoryId = when (routerFlow.state.value) {
            is Screen.Category.Detail -> (routerFlow.state.value as Screen.Category.Detail).id
            else -> -1
        }
    }
}

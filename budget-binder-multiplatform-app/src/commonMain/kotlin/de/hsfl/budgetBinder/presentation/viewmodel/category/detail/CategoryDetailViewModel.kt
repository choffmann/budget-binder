package de.hsfl.budgetBinder.presentation.viewmodel.category.detail

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.GetAllEntriesByCategoryUseCase
import de.hsfl.budgetBinder.domain.usecase.GetCategoryByIdUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoryDetailViewModel(
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
    private val getAllEntriesByCategoryUseCase: GetAllEntriesByCategoryUseCase,
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope
) {
    private val _categoryState =
        MutableStateFlow(Category(id = -1, name = "0", color = "111111", image = Category.Image.DEFAULT, budget = 0f))
    val categoryState: StateFlow<Category> = _categoryState

    private val _entryList = MutableStateFlow<List<Entry>>(emptyList())
    val entryList: StateFlow<List<Entry>> = _entryList


    fun onEvent(event: CategoryDetailEvent) {
        when (event) {
            is CategoryDetailEvent.OnEdit -> routerFlow.navigateTo(Screen.Category.Edit)
            is CategoryDetailEvent.OnDelete -> {}
            is CategoryDetailEvent.OnEntry -> { /* TODO: Navigate to Entry detail */
            }
        }
    }

    private fun deleteCurrentEntry() {

    }


}

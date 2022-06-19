package de.hsfl.budgetBinder.presentation.viewmodel.category

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.usecase.GetAllCategoriesUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategorySummaryViewModel(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope
) {

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList

    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = UiEventSharedFlow.eventFlow

    init {
        getAllCategories()
    }

    fun onEvent(event: CategorySummaryEvent) {
        when (event) {
            is CategorySummaryEvent.OnCategory -> routerFlow.navigateTo(Screen.Category.Detail(event.id))
            is CategorySummaryEvent.OnCategoryCreate -> routerFlow.navigateTo(Screen.Category.Summary)
            is CategorySummaryEvent.OnRefresh -> getAllCategories()
        }
    }

    private fun getAllCategories() = scope.launch {
        getAllCategoriesUseCase.categories().collect { response ->
            when (response) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                is DataResponse.Unauthorized -> {
                    _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                    routerFlow.navigateTo(Screen.Login)
                }
                is DataResponse.Success -> {
                    _categoryList.value = response.data!!
                    _eventFlow.emit(UiEvent.ShowSuccess("TODO: Change to HideSuccess"))
                }
            }
        }
    }
}

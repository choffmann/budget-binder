package de.hsfl.budgetBinder.presentation.viewmodel.category

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.CategoriesUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

open class CategoryViewModel(
    _categoriesUseCases: CategoriesUseCases,
    _scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob()),
    _routerFlow: RouterFlow
) {
    private val scope: CoroutineScope = _scope
    private val categoriesUseCases: CategoriesUseCases = _categoriesUseCases
    private val routerFlow: RouterFlow = _routerFlow

    protected val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = UiEventSharedFlow.eventFlow

    protected fun getAll(onSuccess: (List<Category>) -> Unit) = scope.launch {
        categoriesUseCases.getAllCategoriesUseCase.categories().collect { response ->
            handleDataResponse(response = response, onSuccess = onSuccess)
        }
    }

    protected fun getById(id: Int, onSuccess: (Category) -> Unit) = scope.launch {
        categoriesUseCases.getCategoryByIdUseCase(id = id).collect { response ->
            handleDataResponse(response = response, onSuccess = onSuccess)
        }
    }

    protected fun create(category: Category.In, onSuccess: (Category) -> Unit) = scope.launch {
        categoriesUseCases.createCategoryUseCase(category = category).collect { response ->
            handleDataResponse(response = response, onSuccess = onSuccess)
        }
    }

    protected fun change(id: Int, category: Category.Patch, onSuccess: (Category) -> Unit) = scope.launch {
        categoriesUseCases.changeCategoryByIdUseCase(id = id, category = category).collect { response ->
            handleDataResponse(response = response, onSuccess = onSuccess)
        }
    }

    protected fun delete(id: Int, onSuccess: (Category) -> Unit) = scope.launch {
        categoriesUseCases.deleteCategoryByIdUseCase(id = id).collect { response ->
            handleDataResponse(response = response, onSuccess = onSuccess)
        }
    }

    protected fun entries(id: Int, onSuccess: (List<Entry>) -> Unit) = scope.launch {
        categoriesUseCases.getAllEntriesByCategoryUseCase(id = id).collect { response ->
            handleDataResponse(response = response, onSuccess = onSuccess)
        }
    }

    private fun <T> handleDataResponse(response: DataResponse<T>, onSuccess: (T) -> Unit) = scope.launch {
        when (response) {
            is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
            is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
            is DataResponse.Success -> {
                _eventFlow.emit(UiEvent.HideSuccess)
                onSuccess(response.data!!)
            }
            is DataResponse.Unauthorized -> {
                routerFlow.navigateTo(Screen.Login)
                _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
            }
        }
    }
}

package de.hsfl.budgetBinder.presentation.viewmodel.category

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.CategoriesUseCases
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

    val eventFlow = UiEventSharedFlow.mutableEventFlow

    protected fun getAll(onSuccess: (List<Category>) -> Unit) = scope.launch {
        categoriesUseCases.getAllCategoriesUseCase()
            .collect { it.handleDataResponse<List<Category>>(routerFlow = routerFlow, onSuccess = onSuccess) }
    }

    protected fun getById(id: Int, onSuccess: (Category) -> Unit) = scope.launch {
        categoriesUseCases.getCategoryByIdUseCase(id = id)
            .collect { it.handleDataResponse<Category>(routerFlow = routerFlow, onSuccess = onSuccess) }
    }

    protected fun create(category: Category.In, onSuccess: (Category) -> Unit) = scope.launch {
        categoriesUseCases.createCategoryUseCase(category = category)
            .collect { it.handleDataResponse<Category>(routerFlow = routerFlow, onSuccess = onSuccess) }
    }

    protected fun change(id: Int, category: Category.Patch, onSuccess: (Category) -> Unit) = scope.launch {
        categoriesUseCases.changeCategoryByIdUseCase(id = id, category = category)
            .collect { it.handleDataResponse<Category>(routerFlow = routerFlow, onSuccess = onSuccess) }
    }

    protected fun delete(id: Int, onSuccess: (Category) -> Unit) = scope.launch {
        categoriesUseCases.deleteCategoryByIdUseCase(id = id)
            .collect { it.handleDataResponse<Category>(routerFlow = routerFlow, onSuccess = onSuccess) }
    }

    protected fun entries(id: Int, onSuccess: (List<Entry>) -> Unit) = scope.launch {
        categoriesUseCases.getAllEntriesByCategoryUseCase(id = id)
            .collect { it.handleDataResponse<List<Entry>>(routerFlow = routerFlow, onSuccess = onSuccess) }
    }
}

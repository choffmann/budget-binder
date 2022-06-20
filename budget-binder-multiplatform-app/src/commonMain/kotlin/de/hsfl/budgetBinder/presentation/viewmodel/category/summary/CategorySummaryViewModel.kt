package de.hsfl.budgetBinder.presentation.viewmodel.category.summary

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.domain.usecase.CategoriesUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategorySummaryViewModel(
    categoriesUseCases: CategoriesUseCases,
    scope: CoroutineScope,
    private val routerFlow: RouterFlow
) : CategoryViewModel(
    _routerFlow = routerFlow,
    _categoriesUseCases = categoriesUseCases,
    _scope = scope
) {
    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList

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

    private fun getAllCategories() {
        super.getAll(onSuccess = { _categoryList.value = it })
    }
}

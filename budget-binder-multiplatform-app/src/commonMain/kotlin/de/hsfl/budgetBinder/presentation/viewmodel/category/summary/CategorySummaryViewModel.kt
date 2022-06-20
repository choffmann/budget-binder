package de.hsfl.budgetBinder.presentation.viewmodel.category.summary

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.usecase.CategoriesUseCases
import de.hsfl.budgetBinder.domain.usecase.DeleteCategoryByIdUseCase
import de.hsfl.budgetBinder.domain.usecase.GetAllCategoriesUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategorySummaryViewModel(
    categoriesUseCases: CategoriesUseCases,
    scope: CoroutineScope
): CategoryViewModel(
    _categoriesUseCases =  categoriesUseCases,
    _scope = scope
) {
    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList: StateFlow<List<Category>> = _categoryList

    init {
        getAllCategories()
    }

    fun onEvent(event: CategorySummaryEvent) {
        when (event) {
            is CategorySummaryEvent.OnCategory -> RouterFlow.navigateTo(Screen.Category.Detail(event.id))
            is CategorySummaryEvent.OnCategoryCreate -> RouterFlow.navigateTo(Screen.Category.Summary)
            is CategorySummaryEvent.OnRefresh -> getAllCategories()
        }
    }

    private fun getAllCategories() {
        super.getAll(onSuccess = {_categoryList.value = it})
    }
}

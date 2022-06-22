package de.hsfl.budgetBinder.presentation.viewmodel.category.create

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.domain.usecase.CategoriesUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.handleLifeCycle
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoryCreateViewModel(
    _categoriesUseCases: CategoriesUseCases, _scope: CoroutineScope, private val routerFlow: RouterFlow
) : CategoryDetailViewModel(
    _categoriesUseCases = _categoriesUseCases, _scope = _scope, routerFlow = routerFlow
) {
    private val _categoryNameState = MutableStateFlow("")
    val categoryNameState: StateFlow<String> = _categoryNameState

    private val _categoryColorState = MutableStateFlow("")
    val categoryColorState: StateFlow<String> = _categoryColorState

    private val _categoryImageState = MutableStateFlow(Category.Image.DEFAULT)
    val categoryImageState: StateFlow<Category.Image> = _categoryImageState

    private val _categoryBudgetState = MutableStateFlow(0f)
    val categoryBudgetState: StateFlow<Float> = _categoryBudgetState

    fun onEvent(event: CategoryCreateEvent) {
        when (event) {
            is CategoryCreateEvent.EnteredCategoryName -> _categoryNameState.value = event.value
            is CategoryCreateEvent.EnteredCategoryColor -> _categoryColorState.value = event.value
            is CategoryCreateEvent.EnteredCategoryImage -> _categoryImageState.value = event.value
            is CategoryCreateEvent.EnteredCategoryBudget -> _categoryBudgetState.value = event.value
            is CategoryCreateEvent.OnSave -> super.create(category = createCategoryInFormState(),
                // TODO: Check from EntryCreate or from Summary
                onSuccess = { routerFlow.navigateTo(Screen.Category.Detail(it.id)) })
            // TODO: Check from EntryCreate or from Summary
            is CategoryCreateEvent.OnCancel -> routerFlow.navigateTo(Screen.Category.Summary)
            is CategoryCreateEvent.LifeCycle -> event.value.handleLifeCycle(onLaunch = { },
                onDispose = { resetStateFlows() })
        }
    }

    private fun resetStateFlows() {
        _categoryNameState.value = ""
        _categoryColorState.value = ""
        _categoryImageState.value = Category.Image.DEFAULT
        _categoryBudgetState.value = 0f
    }


    private fun createCategoryInFormState(): Category.In {
        return Category.In(
            name = categoryNameState.value,
            color = categoryColorState.value,
            image = categoryImageState.value,
            budget = categoryBudgetState.value
        )
    }
}

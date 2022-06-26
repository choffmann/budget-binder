package de.hsfl.budgetBinder.presentation.viewmodel.category.edit

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.CategoriesUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.handleLifeCycle
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.CategoryViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoryEditViewModel(
    _categoriesUseCases: CategoriesUseCases,
    _scope: CoroutineScope,
    private val routerFlow: RouterFlow
) : CategoryDetailViewModel(
    _categoriesUseCases = _categoriesUseCases,
    _scope = _scope,
    routerFlow = routerFlow
) {
    private val _categoryNameState = MutableStateFlow("")
    val categoryNameState: StateFlow<String> = _categoryNameState

    private val _categoryColorState = MutableStateFlow("")
    val categoryColorState: StateFlow<String> = _categoryColorState

    private val _categoryImageState = MutableStateFlow(Category.Image.DEFAULT)
    val categoryImageState: StateFlow<Category.Image> = _categoryImageState

    private val _categoryBudgetState = MutableStateFlow(0f)
    val categoryBudgetState: StateFlow<Float> = _categoryBudgetState

    fun onEvent(event: CategoryEditEvent) {
        when (event) {
            is CategoryEditEvent.EnteredCategoryName -> _categoryNameState.value = event.value
            is CategoryEditEvent.EnteredCategoryColor -> _categoryColorState.value = event.value
            is CategoryEditEvent.EnteredCategoryImage -> _categoryImageState.value = event.value
            is CategoryEditEvent.EnteredCategoryBudget -> _categoryBudgetState.value = event.value
            is CategoryEditEvent.OnSave -> super.change(
                id = currentCategoryId,
                category = createCategoryPatchFromState(),
                onSuccess = { routerFlow.navigateTo(Screen.Category.Detail(it.id)) }
            )
            is CategoryEditEvent.OnCancel -> routerFlow.navigateTo(Screen.Category.Detail(currentCategoryId))
            is CategoryEditEvent.OnDelete -> super.delete(
                id = currentCategoryId,
                onSuccess = { routerFlow.navigateTo(Screen.Category.Summary) }
            )
            is CategoryEditEvent.LifeCycle -> event.value.handleLifeCycle(
                onLaunch = { initStateFlows() },
                onDispose = { resetStateFlows() }
            )
        }
    }

    private fun initStateFlows() {
        super.updateCurrentCategoryId()
        super.getById(id = currentCategoryId, onSuccess = { category ->
            _categoryState.value = category
            _categoryNameState.value = _categoryState.value.name
            _categoryColorState.value = _categoryState.value.color
            _categoryImageState.value = _categoryState.value.image
            _categoryBudgetState.value = _categoryState.value.budget
        })
    }

    private fun resetStateFlows() {
        _categoryState.value = initCategory
        _categoryNameState.value = ""
        _categoryColorState.value = ""
        _categoryImageState.value = Category.Image.DEFAULT
        _categoryBudgetState.value = 0f
    }

    private fun createCategoryPatchFromState(): Category.Patch {
        return Category.Patch(
            name = categoryNameState.value,
            color = categoryColorState.value,
            image = categoryImageState.value,
            budget = categoryBudgetState.value
        )
    }
}

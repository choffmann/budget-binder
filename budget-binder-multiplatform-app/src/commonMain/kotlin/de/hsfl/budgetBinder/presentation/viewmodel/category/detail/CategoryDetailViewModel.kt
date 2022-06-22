package de.hsfl.budgetBinder.presentation.viewmodel.category.detail

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.CategoriesUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.handleLifeCycle
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.CategoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class CategoryDetailViewModel(
    _categoriesUseCases: CategoriesUseCases,
    _scope: CoroutineScope,
    private val routerFlow: RouterFlow
) : CategoryViewModel(
    _categoriesUseCases = _categoriesUseCases,
    _scope = _scope,
    _routerFlow = routerFlow
) {
    protected var currentCategoryId = -1
    protected val initCategory =
        Category(id = -1, name = "init", color = "111111", image = Category.Image.DEFAULT, budget = 0f)
    protected val _categoryState = MutableStateFlow(initCategory)
    val categoryState: StateFlow<Category> = _categoryState

    private val _budgetOnAllEntries = MutableStateFlow(0f)
    val budgetOnAllEntries: StateFlow<Float> = _budgetOnAllEntries

    private val _entryList = MutableStateFlow<List<Entry>>(emptyList())
    val entryList: StateFlow<List<Entry>> = _entryList


    /**
     * OnEdit => Navigate to edit category screen
     *
     * OnDelete => Delete the current category
     *
     * OnBack => Navigate to category summary screen
     *
     * OnRefresh => Update the Flows, fetch data from api and emit into flows
     *
     * OnEntry => Navigate to Entry Detail Screen from this Entry
     *
     * Lifecycle => On Launch, fetch data from api and emit into flows
     */
    fun onEvent(event: CategoryDetailEvent) {
        when (event) {
            is CategoryDetailEvent.OnEdit -> routerFlow.navigateTo(Screen.Category.Edit(currentCategoryId))
            is CategoryDetailEvent.OnDelete -> super.delete(
                id = currentCategoryId,
                onSuccess = { routerFlow.navigateTo(Screen.Category.Summary) })
            is CategoryDetailEvent.OnBack -> routerFlow.navigateTo(Screen.Category.Summary)
            is CategoryDetailEvent.OnRefresh -> initStateFlows()
            is CategoryDetailEvent.OnEntry -> { /* TODO: Navigate to Entry detail */
            }
            is CategoryDetailEvent.LifeCycle -> event.value.handleLifeCycle(
                onLaunch = { initStateFlows() },
                onDispose = { resetStateFlows() }
            )
        }
    }

    /**
     * Init state flow
     * 1. Update the current category id
     * 2. Emit the category callback into the category state
     * 3. Emit the entry callback into the entryList state
     */
    private fun initStateFlows() {
        updateCurrentCategoryId()
        getCategoryByCurrentId()
        setEntryList()
    }

    private fun resetStateFlows() {
        _categoryState.value = initCategory
        _entryList.value = emptyList()
    }

    private fun getCategoryByCurrentId() {
        super.getById(id = currentCategoryId, onSuccess = { _categoryState.value = it })
    }

    private fun setEntryList() {
        super.entries(id = currentCategoryId, onSuccess = {
            _entryList.value = it
            calculateBudget(it)
        })
    }

    private fun calculateBudget(entryList: List<Entry>) {
        var spendMoney = 0F
        entryList.onEach {
            if (it.amount > 0) {
                spendMoney -= it.amount
            } else {
                spendMoney += (it.amount * -1)
            }
        }
        _budgetOnAllEntries.value = spendMoney
    }

    /**
     * Update internal category id, which was set on the router screen state
     * to fetch data from backend with this category id
     */
    protected fun updateCurrentCategoryId() {
        currentCategoryId = when (routerFlow.state.value) {
            is Screen.Category.Detail -> (routerFlow.state.value as Screen.Category.Detail).id
            is Screen.Category.Edit -> (routerFlow.state.value as Screen.Category.Edit).id
            else -> -1
        }
    }
}

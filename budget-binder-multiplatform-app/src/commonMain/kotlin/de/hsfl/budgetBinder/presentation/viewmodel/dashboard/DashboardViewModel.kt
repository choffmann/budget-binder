package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.event.handleLifeCycle
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import io.ktor.util.date.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardUseCases: DashboardUseCases,
    private val logoutUseCase: LogoutUseCase,
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope
) {

    private var internalCategoryId = -1
    private val currentMonth: Month = GMTDate().month
    private val currentYear: Int = GMTDate().year
    private var lastRequestedMonth = currentMonth
    private var lastRequestedYear = currentYear

    private val _categoryListState = MutableStateFlow(DashboardState().categoryList)
    private val _entryListState = MutableStateFlow(DashboardState().entryList)
    val entryListState: StateFlow<List<DashboardEntryState>> = _entryListState

    private val _oldEntriesMapState = MutableStateFlow(DashboardState().oldEntriesList)
    val oldEntriesMapState: StateFlow<Map<String, DashboardState>> = _oldEntriesMapState

    private val _focusedCategoryState = MutableStateFlow(DashboardState().focusedCategory)
    val focusedCategoryState: StateFlow<Category> = _focusedCategoryState

    private val _spendBudgetOnCurrentCategory =
        MutableStateFlow(DashboardState().spendBudgetOnCurrentCategory)
    val spendBudgetOnCurrentCategory: StateFlow<Float> = _spendBudgetOnCurrentCategory

    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.OnNextCategory -> changedFocusedCategory(increase = true)
            is DashboardEvent.OnPrevCategory -> changedFocusedCategory(increase = false)
            is DashboardEvent.OnEntry -> routerFlow.navigateTo(Screen.Entry.Overview(event.id))
            is DashboardEvent.OnEntryCreate -> routerFlow.navigateTo(Screen.Entry.Create)
            is DashboardEvent.OnRefresh -> refresh()
            is DashboardEvent.OnLoadMore -> loadMoreEntries()
            is DashboardEvent.OnEntryDelete -> deleteEntry(id = event.id)
            is DashboardEvent.LifeCycle -> event.value.handleLifeCycle(onLaunch = { initStateFlows() },
                onDispose = { resetStateFlows() })
        }
    }

    private fun initStateFlows() {
        getAllCategories(onSuccess = { categories ->
            _categoryListState.value = categories
            setOverallCategoryState()
        })
    }

    private fun resetStateFlows() {
        resetOldEntries()
        _categoryListState.value = emptyList()
        _entryListState.value = DashboardState().entryList
        _focusedCategoryState.value = DashboardState().focusedCategory
        _spendBudgetOnCurrentCategory.value = DashboardState().spendBudgetOnCurrentCategory
    }

    private fun fillEntryListStateWithResult(entryList: List<Entry>) {
        _entryListState.value = mapEntryListToDashboardEntryState(entryList)
    }

    private fun fillOldEntriesMapState(period: String, entryList: List<Entry>) {
        _oldEntriesMapState.value = oldEntriesMapState.value.toMutableMap().apply {
            putAll(
                mapOf(
                    Pair(
                        period,
                        DashboardState(entryList = mapEntryListToDashboardEntryState(entryList))
                    )
                )
            )
        }
    }

    private fun refresh() {
        when (internalCategoryId) {
            -1 -> getAllEntries(onSuccess = { fillEntryListStateWithResult(it) })
            in _categoryListState.value.indices -> getEntriesByCategory(
                id = focusedCategoryState.value.id,
                onSuccess = { fillEntryListStateWithResult(it) })
            _categoryListState.value.size -> getEntriesByCategory(
                id = null,
                onSuccess = { fillEntryListStateWithResult(it) })
        }
    }

    private fun getAllEntries(onSuccess: (List<Entry>) -> Unit) = scope.launch {
        dashboardUseCases.getAllEntriesUseCase().collect {
            it.handleDataResponse<List<Entry>>(routerFlow = routerFlow, onSuccess = onSuccess)
        }
    }


    private fun getAllCategories(onSuccess: (List<Category>) -> Unit) = scope.launch {
        dashboardUseCases.getAllCategoriesUseCase().collect {
            it.handleDataResponse<List<Category>>(routerFlow = routerFlow, onSuccess = onSuccess)
        }
    }


    private fun getEntriesByCategory(
        id: Int? = null,
        period: String? = null,
        onSuccess: (List<Entry>) -> Unit
    ) =
        scope.launch {
            dashboardUseCases.getAllEntriesByCategoryUseCase(id, period).collect {
                it.handleDataResponse<List<Entry>>(routerFlow = routerFlow, onSuccess = onSuccess)
            }
        }


    private fun getAllEntriesFromMonth(period: String, onSuccess: (List<Entry>) -> Unit) =
        scope.launch {
            dashboardUseCases.getAllEntriesUseCase(period).collect {
                it.handleDataResponse<List<Entry>>(
                    routerFlow = routerFlow, onSuccess = onSuccess
                )
            }
        }

    private fun deleteEntry(id: Int) = scope.launch {
        dashboardUseCases.deleteEntryByIdUseCase(id).collect {
            handleDataResponse(response = it, onSuccess = {
                scope.launch {
                    _eventFlow.emit(UiEvent.ShowSuccess("Removed Category"))
                }
                onEvent(DashboardEvent.OnRefresh)
            })
        }
    }

    private suspend fun <T> handleDataResponse(response: DataResponse<T>, onSuccess: (T) -> Unit) {
        when (response) {
            is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
            is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
            is DataResponse.Success -> {
                _eventFlow.emit(UiEvent.HideSuccess)
                onSuccess(response.data!!)
            }
            is DataResponse.Unauthorized -> {
                _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                routerFlow.navigateTo(Screen.Login)
            }
        }
    }

    private fun mapEntryListToDashboardEntryState(entryList: List<Entry>): List<DashboardEntryState> {
        return entryList.map { entry ->
            val category = getCategoryByEntry(entry)
            DashboardEntryState(
                entry,
                categoryImage = category?.image ?: DashboardEntryState(entry).categoryImage,
                categoryColor = category?.color ?: DashboardEntryState(entry).categoryColor
            )
        }
    }

    private fun getCategoryByEntry(entry: Entry): Category? {
        _categoryListState.value.forEach { category ->
            if (category.id == entry.category_id) return category
        }
        return null
    }

    private fun changedFocusedCategory(increase: Boolean) {
        changeInternalCategoryId(increase)
        when (internalCategoryId) {
            -1 -> setOverallCategoryState()
            in _categoryListState.value.indices -> setCategoryState()
            _categoryListState.value.size -> setCategoryWithNoCategory()
        }
        resetOldEntries()
    }

    private fun resetOldEntries() {
        _oldEntriesMapState.value = mutableMapOf()
        lastRequestedMonth = currentMonth
        lastRequestedYear = currentYear
    }

    /**
     * Change the internal id to swipe between the categories
     * @param increase if the next or prev button is pressed
     * @author Cedrik Hoffmann
     */
    private fun changeInternalCategoryId(increase: Boolean) {
        var newFocusedCategory = internalCategoryId
        if (increase) newFocusedCategory++
        else newFocusedCategory--
        internalCategoryId = when {
            newFocusedCategory < -1 -> -1
            newFocusedCategory > _categoryListState.value.size -> _categoryListState.value.size
            else -> newFocusedCategory
        }
    }

    /**
     * Set the first category, all entries are here
     */
    private fun setOverallCategoryState() {
        getAllEntries(onSuccess = { entryList ->
            var totalBudget = 0f
            _categoryListState.value.forEach { totalBudget += it.budget }
            _focusedCategoryState.value = focusedCategoryState.value.copy(
                hasPrev = false,
                hasNext = true,
                category = Category(0, "Overall", "1675d1", Category.Image.DEFAULT, totalBudget)
            )
            calcSpendBudgetOnCategory(entryList)
            fillEntryListStateWithResult(entryList)
        })
    }

    /**
     * Set the category state for the current category, which the user moved to
     */
    private fun setCategoryState() {
        getEntriesByCategory(id = _categoryListState.value[internalCategoryId].id, onSuccess = {
            calcSpendBudgetOnCategory(it)
            _focusedCategoryState.value = focusedCategoryState.value.copy(
                hasPrev = true,
                hasNext = true,
                category = _categoryListState.value[internalCategoryId]
            )
            fillEntryListStateWithResult(it)
        })
    }

    /**
     * All entries with no category are shown in this category
     */
    private fun setCategoryWithNoCategory() {
        getEntriesByCategory(id = null, onSuccess = {
            _focusedCategoryState.value = focusedCategoryState.value.copy(
                hasPrev = true,
                hasNext = false,
                category = Category(0, "No Category", "111111", Category.Image.DEFAULT, 0f)
            )
            fillEntryListStateWithResult(it)
        })
    }

    /**
     * Calculate the spend money for a category
     */
    private fun calcSpendBudgetOnCategory(entryList: List<Entry>) {
        var spendMoney = 0F
        entryList.onEach {
            if (it.amount > 0) {
                spendMoney -= it.amount
            } else {
                spendMoney += (it.amount * -1)
            }
        }
        _spendBudgetOnCurrentCategory.value =
            spendBudgetOnCurrentCategory.value
    }

    /**
     * First, calculate the next Month and Year to request
     * After that, show on which category we are at the moment and fetch old entries
     */
    private fun loadMoreEntries() {
        val nextMonth = when {
            // When the month goes from 01 to 12, the year has to be changed
            lastRequestedMonth.ordinal - 1 < 0 -> {
                lastRequestedYear -= 1
                11
            }
            else -> lastRequestedMonth.ordinal - 1
        }
        val periodString = "${Month.from(nextMonth).toMonthString()}-${lastRequestedYear}"
        when (internalCategoryId) {
            -1 -> getAllEntriesFromMonth(period = periodString) {
                fillOldEntriesMapState(periodString, it)
            }
            in _categoryListState.value.indices -> getEntriesByCategory(id = focusedCategoryState.value.id,
                period = periodString,
                onSuccess = { fillOldEntriesMapState(periodString, it) })
            _categoryListState.value.size -> getEntriesByCategory(
                id = null,
                period = periodString,
                onSuccess = { fillOldEntriesMapState(periodString, it) })
        }
        lastRequestedMonth = Month.from(nextMonth)
    }


    /**
     * Helper Function to convert object in String for ApiRequest
     * Month.ordinal starts at 0, so we have to add by 1 every time
     * if (Month.ordinal + 1) < 10 then add a 0 at the front
     * else return the ordinal
     */
    private fun Month.toMonthString(): String {
        return when {
            this.ordinal + 1 < 10 -> "0${this.ordinal + 1}"
            else -> "${this.ordinal + 1}"
        }
    }
}

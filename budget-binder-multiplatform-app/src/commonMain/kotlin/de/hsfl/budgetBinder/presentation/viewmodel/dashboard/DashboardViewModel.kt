package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
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
    private val dataFlow: DataFlow,
    private val scope: CoroutineScope
) {

    private var internalCategoryId = -1
    private val currentMonth: Month = GMTDate().month
    private var lastRequestedMonth = currentMonth

    private val _categoryListState = MutableStateFlow<List<Category>>(emptyList())
    private val _entryListState = MutableStateFlow(DashboardState())
    val entryListState: StateFlow<DashboardState> = _entryListState

    private val _oldEntriesMapState = MutableStateFlow<MutableMap<String, List<Entry>>>(mutableMapOf())
    val oldEntriesMapState: StateFlow<Map<String, List<Entry>>> = _oldEntriesMapState

    private val _focusedCategoryState = MutableStateFlow(DashboardState())
    val focusedCategoryState: StateFlow<DashboardState> = _focusedCategoryState

    private val _spendBudgetOnCurrentCategory = MutableStateFlow(DashboardState())
    val spendBudgetOnCurrentCategory: StateFlow<DashboardState> = _spendBudgetOnCurrentCategory

    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        // Throws nullPointerException, crash on Android?
        //_getAllEntries()
        //_getAllCategories()

        getAllCategories()
        getAllEntries()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.OnNextCategory -> changedFocusedCategory(increase = true)
            is DashboardEvent.OnPrevCategory -> changedFocusedCategory(increase = false)
            is DashboardEvent.OnEntry -> {}
            is DashboardEvent.OnEntryCreate -> scope.launch {
                _eventFlow.emit(UiEvent.ShowError(currentMonth.toMonthString()))
            }
            is DashboardEvent.OnRefresh -> {
                getAllCategories()
                when (internalCategoryId) {
                    -1 -> getAllEntries()
                    in _categoryListState.value.indices -> getEntriesByCategory(id = focusedCategoryState.value.category.id)
                    _categoryListState.value.size -> getEntriesByCategory(id = null)
                }
            }
            is DashboardEvent.OnLoadMore -> loadMoreEntries()
            is DashboardEvent.OnEntryDelete -> deleteEntry(id = event.id)
        }
    }

    private fun getAllEntries() {
        scope.launch {
            dashboardUseCases.getAllEntriesUseCase.entries().collect {
                when (it) {
                    is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                    is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                    is DataResponse.Success -> {
                        _entryListState.value = entryListState.value.copy(entryList = it.data!!.map { entry ->
                            DashboardEntryState(
                                entry,
                                categoryImage = getCategoryByEntry(entry)?.image ?: Category.Image.DEFAULT
                            )
                        })
                        _eventFlow.emit(UiEvent.HideSuccess)
                        calcSpendBudgetOnCategory()
                    }
                    is DataResponse.Unauthorized -> {
                        _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                        routerFlow.navigateTo(Screen.Login)
                    }
                }
            }
        }
    }

    private fun getAllCategories() {
        scope.launch {
            dashboardUseCases.getAllCategoriesUseCase.categories().collect {
                when (it) {
                    is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                    is DataResponse.Error-> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                    is DataResponse.Success -> {
                        _categoryListState.value = it.data!!
                        _eventFlow.emit(UiEvent.HideSuccess)
                    }
                    is DataResponse.Unauthorized -> {
                        _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                        routerFlow.navigateTo(Screen.Login)
                    }
                }
            }
        }
    }

    private fun getEntriesByCategory(id: Int? = null) {
        scope.launch {
            dashboardUseCases.getAllEntriesByCategoryUseCase(id).collect {
                when (it) {
                    is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                    is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                    is DataResponse.Success -> {
                        _entryListState.value = entryListState.value.copy(entryList = it.data!!.map { entry ->
                            DashboardEntryState(
                                entry,
                                categoryImage = getCategoryByEntry(entry)?.image ?: Category.Image.DEFAULT
                            )
                        })
                        _eventFlow.emit(UiEvent.HideSuccess)
                        calcSpendBudgetOnCategory()
                    }
                    is DataResponse.Unauthorized -> {
                        _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                        routerFlow.navigateTo(Screen.Login)
                    }
                }
            }
        }
    }

    private fun getAllEntriesFromMonth(month: Month) = scope.launch {
        // TODO: Year had also be to check
        dashboardUseCases.getAllEntriesUseCase.entries("${month.toMonthString()}-${GMTDate().year}").collect {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success -> {
                    _eventFlow.emit(UiEvent.HideSuccess)
                    _oldEntriesMapState.value.putAll(
                        mapOf(
                            Pair(
                                "${month.toMonthString()}-${GMTDate().year}",
                                it.data!!
                            )
                        )
                    )
                }
                is DataResponse.Unauthorized -> {
                    _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                    routerFlow.navigateTo(Screen.Login)
                }
            }
        }
    }

    private fun deleteEntry(id: Int) = scope.launch {
        dashboardUseCases.deleteEntryByIdUseCase(id).collect {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error, is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success -> {
                    _eventFlow.emit(UiEvent.ShowSuccess("Removed Category"))
                    onEvent(DashboardEvent.OnRefresh)
                }
            }
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
    }

    /**
     * Change the internal Id to swipe between the categories
     * @param increase if the next or prev button is pressed
     * @author Cedrik Hoffmann
     */
    private fun changeInternalCategoryId(increase: Boolean) {
        var newFocusedCategory = internalCategoryId
        if (increase)
            newFocusedCategory++
        else
            newFocusedCategory--
        internalCategoryId =
            when {
                newFocusedCategory < -1 -> -1
                newFocusedCategory > _categoryListState.value.size -> _categoryListState.value.size
                else -> newFocusedCategory
            }
    }

    /**
     * Set the first category, all entries are here
     */
    private fun setOverallCategoryState() {
        var totalBudget = 0f
        _categoryListState.value.forEach { totalBudget += it.budget }
        _focusedCategoryState.value = focusedCategoryState.value.copy(
            hasPrev = false,
            hasNext = true,
            category = Category(0, "Overall", "111111", Category.Image.DEFAULT, totalBudget)
        )
        getAllEntries()
    }

    /**
     * Set the category state for the current category, wich the user moved to
     */
    private fun setCategoryState() {
        _focusedCategoryState.value = focusedCategoryState.value.copy(
            hasPrev = true,
            hasNext = true,
            category = _categoryListState.value[internalCategoryId]
        )
        getEntriesByCategory(id = focusedCategoryState.value.category.id)
    }

    /**
     * All entries with no category are shown in this category
     */
    private fun setCategoryWithNoCategory() {
        _focusedCategoryState.value = focusedCategoryState.value.copy(
            hasPrev = true,
            hasNext = false,
            category = Category(0, "No Category", "111111", Category.Image.DEFAULT, 0f)
        )
        getEntriesByCategory(id = null)
    }

    /**
     * Calculate the spend money for a category
     */
    private fun calcSpendBudgetOnCategory() {
        var spendMoney = 0F
        entryListState.value.entryList.onEach {
            if (it.entry.amount > 0) {
                spendMoney -= it.entry.amount
            } else {
                spendMoney += (it.entry.amount * -1)
            }
        }
        _spendBudgetOnCurrentCategory.value =
            spendBudgetOnCurrentCategory.value.copy(spendBudgetOnCurrentCategory = spendMoney)
    }

    private fun loadMoreEntries() {
        val nextMonth = when {
            lastRequestedMonth.ordinal - 1 < 0 -> 11
            else -> lastRequestedMonth.ordinal - 1
        }
        getAllEntriesFromMonth(
            month = Month.from(nextMonth)
        )
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

    // Old
    private val _categoriesState = MutableStateFlow<UiState>(UiState.Empty)

    @Deprecated(message = "Use new StateFlow")
    val categoriesState: StateFlow<UiState> = _categoriesState

    private val _entriesState = MutableStateFlow<UiState>(UiState.Empty)

    @Deprecated(message = "Use new StateFlow")
    val entriesState: StateFlow<UiState> = _entriesState

    private val _state = MutableStateFlow<UiState>(UiState.Empty)

    @Deprecated(message = "Use new StateFlow")
    val state: StateFlow<UiState> = _state

    fun logOut(onAllDevices: Boolean) {
        logoutUseCase(onAllDevices).onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(error = it.error!!.message)
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Unauthorized -> routerFlow.navigateTo(Screen.Login)
            }
        }.launchIn(scope)
    }

    @Deprecated(message = "Use new StateFlow")
    private fun _getAllCategories() {
        dashboardUseCases.getAllCategoriesUseCase.categories().onEach {
            when (it) {
                is DataResponse.Success -> _categoriesState.value = UiState.Success(it.data)
                is DataResponse.Error -> _categoriesState.value = UiState.Error(it.error!!.message)
                is DataResponse.Loading -> _categoriesState.value = UiState.Loading
                is DataResponse.Unauthorized -> _categoriesState.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }

    @Deprecated(message = "Use new StateFlow")
    private fun _getAllEntries() {
        dashboardUseCases.getAllEntriesUseCase.entries().onEach {
            when (it) {
                is DataResponse.Success -> _entriesState.value = UiState.Success(it.data)
                is DataResponse.Error -> _entriesState.value = UiState.Error(it.error!!.message)
                is DataResponse.Loading -> _entriesState.value = UiState.Loading
                is DataResponse.Unauthorized -> _entriesState.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }
}

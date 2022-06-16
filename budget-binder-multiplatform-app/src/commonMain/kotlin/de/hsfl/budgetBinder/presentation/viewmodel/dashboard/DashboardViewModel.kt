package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dashboardUseCases: DashboardUseCases,
    private val logoutUseCase: LogoutUseCase,
    private val routerFlow: RouterFlow,
    private val dataFlow: DataFlow,
    private val getMyUserUseCase: GetMyUserUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {


    private val _categoryListState = MutableStateFlow(DashboardState())
    val categoryListSate: StateFlow<DashboardState> = _categoryListState

    private val _entryListState = MutableStateFlow(DashboardState())
    val entryListState: StateFlow<DashboardState> = _entryListState

    private val _focusedCategoryState = MutableStateFlow(DashboardState())
    val focusedCategoryState: StateFlow<DashboardState> = _focusedCategoryState

    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = _eventFlow.asSharedFlow()

    // Everytime the View is open or only once?
    init {
        _getAllEntries()
        _getAllCategories()

        getAllEntries()
        getAllCategories()
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.OnCategoryChanged -> { /* TODO: On Category Changed */
            }
            is DashboardEvent.OnEntry -> { /* TODO: On Entry Clicked */
            }
            is DashboardEvent.OnEntryCreate -> {/* TODO: On Category Create */
            }
        }
    }

    private fun getAllEntries() {
        scope.launch {
            dashboardUseCases.getAllEntriesUseCase.entries().collect {
                when (it) {
                    is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                    is DataResponse.Error, is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                    is DataResponse.Success -> {
                        _entryListState.value = entryListState.value.copy(entryList = it.data!!)
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
                    is DataResponse.Error, is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                    is DataResponse.Success -> {
                        _categoryListState.value = categoryListSate.value.copy(categoryList = it.data!!)
                    }
                }
            }
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

    fun getMyUser() {
        getMyUserUseCase().onEach {
            when (it) {
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Success<*> -> dataFlow.storeUserState(it.data!!)
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
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

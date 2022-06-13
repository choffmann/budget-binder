package de.hsfl.budgetBinder.presentation.viewmodel

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

class DashboardViewModel(
    private val dashboardUseCases: DashboardUseCases,
    private val logoutUseCase: LogoutUseCase,
    private val routerFlow: RouterFlow,
    private val dataFlow: DataFlow,
    private val getMyUserUseCase: GetMyUserUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {

    private val _categoriesState = MutableStateFlow<UiState>(UiState.Empty)
    val categoriesState: StateFlow<UiState> = _categoriesState

    private val _entriesState = MutableStateFlow<UiState>(UiState.Empty)
    val entriesState: StateFlow<UiState> = _entriesState

    // Everytime the View is open or only once?
    init {
        getAllEntries()
        getAllCategories()
    }

    private val _state = MutableStateFlow<UiState>(UiState.Empty)
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
                is DataResponse.Success<*> -> dataFlow.saveUserState(it.data!!)
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
                is DataResponse.Unauthorized -> routerFlow.navigateTo(Screen.Login)
            }
        }.launchIn(scope)

    }

    private fun getAllCategories() {
        dashboardUseCases.getAllCategoriesUseCase.categories().onEach {
            when (it) {
                is DataResponse.Success -> _categoriesState.value = UiState.Success(it.data)
                is DataResponse.Error -> _categoriesState.value = UiState.Error(it.error!!.message)
                is DataResponse.Loading -> _categoriesState.value = UiState.Loading
                is DataResponse.Unauthorized -> _categoriesState.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }

    private fun getAllEntries() {
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
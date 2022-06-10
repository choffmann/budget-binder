package de.hsfl.budgetBinder.presentation.viewmodel

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

class DashboardViewModel(
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {
    private val _state = MutableStateFlow<UiState>(UiState.Empty)
    val state: StateFlow<UiState> = _state

    // Everytime the View is open or only once?
    init {
        getAllEntries()
        getAllCategories()
    }

    private fun getAllCategories() {
        getAllCategoriesUseCase.categories().onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }

    private fun getAllEntries() {
        getAllEntriesUseCase.entries().onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }

    fun logOut(onAllDevices: Boolean) {
        logoutUseCase(onAllDevices).onEach {
            when (it) {
                is DataResponse.Success -> {
                    _state.value = UiState.Success(it.data)
                }
                is DataResponse.Error -> {
                    _state.value = UiState.Error(error = it.message ?: "Something went wrong")
                }
                is DataResponse.Loading -> {
                    _state.value = UiState.Loading
                }
            }
        }.launchIn(scope)
    }
}
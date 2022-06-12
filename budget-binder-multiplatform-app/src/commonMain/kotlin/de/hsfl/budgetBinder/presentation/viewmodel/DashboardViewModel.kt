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

    private fun getAllCategories() {
        getAllCategoriesUseCase.categories().onEach {
            when (it) {
                is DataResponse.Success -> _categoriesState.value = UiState.Success(it.data)
                is DataResponse.Error -> _categoriesState.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _categoriesState.value = UiState.Loading
            }
        }.launchIn(scope)
    }

    private fun getAllEntries() {
        getAllEntriesUseCase.entries().onEach {
            when (it) {
                is DataResponse.Success -> _entriesState.value = UiState.Success(it.data)
                is DataResponse.Error -> _entriesState.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _entriesState.value = UiState.Loading
            }
        }.launchIn(scope)
    }
}
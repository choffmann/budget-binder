package de.hsfl.budgetBinder.presentation.viewmodel

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

class EntryViewModel(
    private val getAllEntriesUseCase: GetAllEntriesUseCase,
    private val getEntryByIdUseCase: GetEntryByIdUseCase,
    private val createNewEntryUseCase: CreateNewEntryUseCase,
    private val changeEntryByIdUseCase: ChangeEntryByIdUseCase,
    private val removeEntryByIdUseCase: RemoveEntryByIdUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {
    private val _state = MutableStateFlow<UiState>(UiState.Empty)
    val state: StateFlow<UiState> = _state

    fun getAllEntries() {
        getAllEntriesUseCase.entries().onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }

    fun getEntryById(id: Int) {
        getEntryByIdUseCase(id).onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }

    fun createEntry(entry: Entry.In) {
        createNewEntryUseCase(entry).onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }

    fun changeEntry(entry: Entry.In, id: Int) {
        changeEntryByIdUseCase(entry, id).onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }

    fun removeEntry(id: Int) {
        removeEntryByIdUseCase(id).onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }
}
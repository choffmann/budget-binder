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

class FtuxViewModel(
    private val createNewEntryUseCase: CreateNewEntryUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {
    private val _state = MutableStateFlow<UiState>(UiState.Empty)
    val state: StateFlow<UiState> = _state

    fun createNewEntries(entries: List<Entry.In>) {
        entries.forEach { entry ->
            createNewEntryUseCase(entry).onEach {
                when (it) {
                    is DataResponse.Success -> _state.value = UiState.Success(it.data)
                    is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                    is DataResponse.Loading -> _state.value = UiState.Loading
                }
            }.launchIn(scope)
        }
    }
}
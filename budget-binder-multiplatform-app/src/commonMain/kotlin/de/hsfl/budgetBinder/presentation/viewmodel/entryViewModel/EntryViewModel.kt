package de.hsfl.budgetBinder.presentation.viewmodel.entryViewModel

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*

class EntryViewModel(
    private val loginUseCases: LoginUseCases,
    private val routerFlow: RouterFlow,
    private val dataFlow: DataFlow,
    private val scope: CoroutineScope
) {
    /* *** Variables *** */

    //Dialog used to confirm deletion
    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState
    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = UiEventSharedFlow.eventFlow


    init {
        //Do we need something to init?
    }

    /* *** Event Handling *** */
    fun onEvent(event: EntryEvent) {
        when (event) {
            is EntryEvent.EnteredName -> {}
            is EntryEvent.EnteredAmount -> {}
            is EntryEvent.EnteredRepeatState -> {}
            is EntryEvent.EnteredCategoryID -> {}
            is EntryEvent.EnteredAmountInputType -> {}
            is EntryEvent.OnCreateEntry -> {}
            is EntryEvent.OnEditEntry -> {}
            is EntryEvent.OnDeleteEntry -> {}
            is EntryEvent.OnDeleteDialogConfirm -> {}
            is EntryEvent.OnDeleteDialogDismiss -> {}
            else -> {
                throw Exception("Unhandled EntryEvent in EntryViewModel")
            }
        }

    }


    /* *** Use Case usages *** */
    fun getEntryById(id: Int) {}
    fun createEntry(entry: Entry.In) {}
    fun changeEntry(entry: Entry.Patch, id: Int) {}
    fun removeEntry(id: Int) {}

    /* *** Helper *** */
    private fun toggleDialog() {
        _dialogState.value = !dialogState.value
    }
}

package de.hsfl.budgetBinder.presentation.viewmodel.entryViewModel

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import de.hsfl.budgetBinder.presentation.viewmodel.login.LoginTextFieldState
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

    // ---- Data Input Variables ----
    private val _nameText = MutableStateFlow(EntryInputState())
    val nameText: StateFlow<EntryInputState> = _nameText

    private val _amountText = MutableStateFlow(EntryInputState())
    val amountText: StateFlow<EntryInputState> = _amountText

    private val _repeatState = MutableStateFlow(EntryInputState())
    val repeatState: StateFlow<EntryInputState> = _repeatState

    private val _categoryIDState = MutableStateFlow(EntryInputState())
    val categoryIDState: StateFlow<EntryInputState> = _categoryIDState

    private val _amountSignState = MutableStateFlow(EntryInputState())
    val amountSignState: StateFlow<EntryInputState> = _amountSignState


    // --- Default ViewModel Variables ----
    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState
    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = UiEventSharedFlow.eventFlow
    // ----

    init {
        //Do we need something to init?
    }

    /* *** Event Handling *** */
    fun onEvent(event: EntryEvent) {
        when (event) {
            is EntryEvent.EnteredName -> {}
            is EntryEvent.EnteredAmount -> {}
            is EntryEvent.EnteredRepeat -> {}
            is EntryEvent.EnteredCategoryID -> {}
            is EntryEvent.EnteredAmountSign -> {}
            is EntryEvent.OnCreateEntry -> {
                when (routerFlow.state.value) {
                    is Screen.Entry.Create -> createEntry(
                        Entry.In(
                            nameText,
                            amountState,
                            repeatState,
                            categoryIDState
                        )
                    )
                    else -> routerFlow.navigateTo(Screen.Entry.Create)
                }
            }
            is EntryEvent.OnEditEntry -> routerFlow.navigateTo(Screen.Settings.Server)
            is EntryEvent.OnDeleteEntry -> _dialogState.value = true
            is EntryEvent.OnDeleteDialogConfirm -> removeEntry()//TODO Entry ID
            is EntryEvent.OnDeleteDialogDismiss -> _dialogState.value = false
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

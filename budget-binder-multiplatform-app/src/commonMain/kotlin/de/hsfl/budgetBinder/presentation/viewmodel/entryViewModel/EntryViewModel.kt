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
    private val entryUseCases: EntryUseCases,
    private val routerFlow: RouterFlow,
    private val dataFlow: DataFlow,
    private val scope: CoroutineScope
) {
    /* *** Variables *** */

    // ---- Data Input Variables ----
    private val _nameText = MutableStateFlow(EntryInputState().name)
    val nameText: StateFlow<String> = _nameText

    private val _amountText = MutableStateFlow(EntryInputState().amount)
    val amountText: StateFlow<Float> = _amountText

    private val _repeatState = MutableStateFlow(EntryInputState().repeat)
    val repeatState: StateFlow<Boolean> = _repeatState

    private val _categoryIDState = MutableStateFlow(EntryInputState().categoryID)
    val categoryIDState: StateFlow<Int?> = _categoryIDState

    private val _amountSignState = MutableStateFlow(EntryInputState().amountSign)
    val amountSignState: StateFlow<Boolean> = _amountSignState

    private val _selectedEntry = MutableStateFlow(EntryState().selectedEntry)
    val selectedEntry: StateFlow<Entry> = _selectedEntry

    // --- Default ViewModel Variables ----
    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState
    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = UiEventSharedFlow.eventFlow
    // ----

    init {
        when (routerFlow.state.value) {
            is Screen.Entry.Overview -> getEntryById((routerFlow.state.value as Screen.Entry.Overview).id)
            is Screen.Entry.Edit -> getEntryById((routerFlow.state.value as Screen.Entry.Edit).id)
            else -> {}
        }
    }

    /* *** Event Handling *** */
    fun onEvent(event: EntryEvent) {
        when (event) {
            is EntryEvent.EnteredName -> _nameText.value = nameText.value
            is EntryEvent.EnteredAmount -> _amountText.value = amountText.value
            is EntryEvent.EnteredRepeat -> _repeatState.value = repeatState.value
            is EntryEvent.EnteredCategoryID -> _categoryIDState.value = categoryIDState.value
            is EntryEvent.EnteredAmountSign -> _amountSignState.value = amountSignState.value
            is EntryEvent.OnCreateEntry ->
                when (routerFlow.state.value) {
                    is Screen.Entry.Create -> createEntry(
                        Entry.In(
                            nameText.value,
                            amountText.value,
                            repeatState.value,
                            categoryIDState.value
                        )
                    )
                    else -> routerFlow.navigateTo(Screen.Entry.Create)
                }

            is EntryEvent.OnEditEntry ->
                when (routerFlow.state.value) {
                    is Screen.Entry.Edit -> changeEntry(
                        Entry.Patch(
                            nameText.value,
                            amountText.value,
                            repeatState.value,
                            Entry.Category(categoryIDState.value)
                        ), selectedEntry.value.id
                    )
                    else -> routerFlow.navigateTo(Screen.Entry.Edit(selectedEntry.value.id)) //using ID seems... unnecessary?
                }
            is EntryEvent.OnDeleteEntry -> _dialogState.value = true
            is EntryEvent.OnDeleteDialogConfirm -> removeEntry(selectedEntry.value.id)
            is EntryEvent.OnDeleteDialogDismiss -> _dialogState.value = false
            else -> {
                throw Exception("Unhandled EntryEvent in EntryViewModel")
            }
        }

    }


    /* *** Use Case usages *** */ //TODO Implement use cases
    fun getEntryById(id: Int) {

    }
    fun createEntry(entry: Entry.In) {}
    fun changeEntry(entry: Entry.Patch, id: Int) {}
    fun removeEntry(id: Int) {}

    /* *** Helper *** */
    private fun toggleDialog() {
        _dialogState.value = !dialogState.value
    }
}

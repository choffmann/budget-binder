package de.hsfl.budgetBinder.presentation.viewmodel.entry

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.event.handleLifeCycle
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class EntryViewModel(
    private val entryUseCases: EntryUseCases,
    private val routerFlow: RouterFlow,
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

    private val _selectedEntryState = MutableStateFlow(EntryState().selectedEntry)
    val selectedEntryState: StateFlow<Entry> = _selectedEntryState

    private val _categoryListState = MutableStateFlow(EntryState().categoryList)
    val categoryListState: StateFlow<List<Category>> = _categoryListState

    // --- Default ViewModel Variables ----
    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState
    val eventFlow = UiEventSharedFlow.mutableEventFlow
    // ----


    /* *** Event Handling *** */
    fun onEvent(event: EntryEvent) {
        when (event) {
            is EntryEvent.EnteredName -> _nameText.value = event.value
            is EntryEvent.EnteredAmount -> _amountText.value = event.value
            is EntryEvent.EnteredRepeat -> _repeatState.value = !repeatState.value
            is EntryEvent.EnteredCategoryID -> _categoryIDState.value = event.value
            is EntryEvent.EnteredAmountSign -> _amountSignState.value = !amountSignState.value
            is EntryEvent.OnCreateEntry -> {
                when (routerFlow.state.value) {
                    is Screen.Entry.Create -> create(
                        Entry.In(
                            nameText.value,
                            buildAmount(),
                            repeatState.value,
                            categoryIDState.value
                        )
                    )
                    else -> routerFlow.navigateTo(Screen.Entry.Create)
                }
            }
            is EntryEvent.OnEditEntry ->
                when (routerFlow.state.value) {
                    is Screen.Entry.Edit -> update(
                        Entry.Patch(
                            nameText.value,
                            buildAmount(),
                            repeatState.value,
                            Entry.Category(categoryIDState.value)
                        ), selectedEntryState.value.id
                    )
                    else -> {
                        routerFlow.navigateTo(Screen.Entry.Edit(selectedEntryState.value.id))
                    } //using ID seems... unnecessary?}
                }
            is EntryEvent.OnDeleteEntry -> _dialogState.value = true
            is EntryEvent.OnDeleteDialogConfirm -> delete(selectedEntryState.value.id)
            is EntryEvent.OnDeleteDialogDismiss -> _dialogState.value = false
            is EntryEvent.OnCancel -> routerFlow.navigateTo(Screen.Dashboard)
            is EntryEvent.LifeCycle -> event.value.handleLifeCycle(
                onLaunch = {
                    getCategoryList()
                    when (routerFlow.state.value) {
                        is Screen.Entry.Overview -> getById((routerFlow.state.value as Screen.Entry.Overview).id)
                        is Screen.Entry.Edit -> getById((routerFlow.state.value as Screen.Entry.Edit).id)
                        else -> {
                        }
                    }
                },
                onDispose = {
                    resetFlows()
                })
        }
    }


    /* *** Use Case usages *** */
    private fun getById(id: Int) = scope.launch {
        entryUseCases.getEntryByIdUseCase(id).collect {
            it.handleDataResponse<Entry>(routerFlow = routerFlow, onSuccess = { entry ->
                _selectedEntryState.value = entry
                _nameText.value = _selectedEntryState.value.name
                _amountText.value = _selectedEntryState.value.amount.absoluteValue
                _amountSignState.value = _selectedEntryState.value.amount >= 0
                _repeatState.value = _selectedEntryState.value.repeat
                _categoryIDState.value = _selectedEntryState.value.category_id
            })
        }
    }

    private fun getCategoryList() = scope.launch {
        entryUseCases.getCategoryListUseCase().collect {
            it.handleDataResponse<List<Category>>(
                routerFlow = routerFlow, onSuccess = { cl -> _categoryListState.value = cl })
        }
    }

    private fun create(entry: Entry.In) = scope.launch {
        entryUseCases.createNewEntryUseCase(entry).collect {
            it.handleDataResponse<Entry>(
                routerFlow = routerFlow, onSuccess = {
                    routerFlow.navigateTo(Screen.Dashboard)
                    eventFlow.emit(UiEvent.ShowSuccess("Entry successfully created"))
                })
        }
    }

    private fun update(entry: Entry.Patch, id: Int) = scope.launch {
        entryUseCases.changeEntryByIdUseCase(entry, id).collect {
            it.handleDataResponse<Entry>(
                routerFlow = routerFlow, onSuccess = {
                    routerFlow.navigateTo(Screen.Dashboard)
                    eventFlow.emit(UiEvent.ShowSuccess("Entry successfully updated"))
                })
        }
    }

    private fun delete(id: Int) = scope.launch {
        entryUseCases.deleteEntryByIdUseCase(id).collect {
            it.handleDataResponse<Entry>(
                routerFlow = routerFlow, onSuccess = {
                    routerFlow.navigateTo(Screen.Dashboard)
                    eventFlow.emit(UiEvent.ShowSuccess("Entry successfully deleted"))
                })
        }
    }


    /**
     * Negates amount if amountSign is false
     *  */
    private fun buildAmount(): Float {
        return if (_amountSignState.value) {
            _amountText.value
        } else {
            _amountText.value * -1
        }
    }

    /**
     * Resets Data Input Variables
     */
    private fun resetFlows() {
        _nameText.value = EntryInputState().name
        _amountText.value = EntryInputState().amount
        _amountSignState.value = EntryInputState().amountSign
        _repeatState.value = EntryInputState().repeat
        _categoryIDState.value = EntryInputState().categoryID
    }

}

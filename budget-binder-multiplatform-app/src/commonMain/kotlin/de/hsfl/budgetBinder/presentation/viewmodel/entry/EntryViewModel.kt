package de.hsfl.budgetBinder.presentation.viewmodel.entry

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.UiEvent
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

    private val _selectedEntryState = MutableStateFlow(EntryState().selectedEntry)
    val selectedEntryState: StateFlow<Entry> = _selectedEntryState

    private val _categoryListState = MutableStateFlow(EntryState().categoryList)
    val categoryListState: StateFlow<List<Category>> = _categoryListState

    // --- Default ViewModel Variables ----
    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState
    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = UiEventSharedFlow.eventFlow
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
                            amountText.value,
                            repeatState.value,
                            Entry.Category(categoryIDState.value)
                        ), selectedEntryState.value.id
                    )
                    else -> {
                        resetFlows()
                        routerFlow.navigateTo(Screen.Entry.Edit(selectedEntryState.value.id))
                    } //using ID seems... unnecessary?}
                }
            is EntryEvent.OnDeleteEntry -> _dialogState.value = true
            is EntryEvent.OnDeleteDialogConfirm -> deleteEntry(selectedEntryState.value.id)
            is EntryEvent.OnDeleteDialogDismiss -> _dialogState.value = false
            is EntryEvent.LoadCreate -> {
                resetFlows()
                getCategoryList()
            }
            is EntryEvent.LoadOverview -> {
                resetFlows()
                getById((routerFlow.state.value as Screen.Entry.Overview).id)
            }
            is EntryEvent.LoadEdit -> {
                resetFlows()
                getCategoryList()
                getById((routerFlow.state.value as Screen.Entry.Edit).id)
            }
        }
    }


    /* *** Use Case usages *** */
    protected fun getById(id: Int) = scope.launch {
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

    protected fun getCategoryList() = scope.launch {
        entryUseCases.getCategoryListUseCase().collect {
            it.handleDataResponse<List<Category>>(
                routerFlow = routerFlow, onSuccess = { cl -> _categoryListState.value = cl })
        }
    }

    fun create(entry: Entry.In) = scope.launch {
        entryUseCases.createNewEntryUseCase(entry).collect {
            it.handleDataResponse<Entry>(
                routerFlow = routerFlow, onSuccess = { routerFlow.navigateTo(Screen.Dashboard) })
        }
    }

    fun update(entry: Entry.Patch, id: Int) {
        scope.launch {
            entryUseCases.changeEntryByIdUseCase(entry, id).collect { response ->
                when (response) {
                    is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                    is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                    is DataResponse.Success<*> -> {
                        _eventFlow.emit(UiEvent.ShowSuccess("Entry successfully updated")) //TODO?: Change the msg
                        routerFlow.navigateTo(Screen.Dashboard)
                    }
                    is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                }
            }
        }
    }

    fun deleteEntry(id: Int) {
        scope.launch {
            entryUseCases.deleteEntryByIdUseCase(id).collect { response ->
                when (response) {
                    is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                    is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                    is DataResponse.Success<*> -> {
                        _eventFlow.emit(UiEvent.ShowSuccess("Entry successfully deleted")) //TODO?: Change the msg
                        routerFlow.navigateTo(Screen.Dashboard)
                    }
                    is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                }
            }
        }
    }

    private suspend fun <T> handleDataResponse(response: DataResponse<T>, onSuccess: (T) -> Unit) {
        when (response) {
            is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
            is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
            is DataResponse.Success -> {
                _eventFlow.emit(UiEvent.HideSuccess)
                onSuccess(response.data!!)
            }
            is DataResponse.Unauthorized -> {
                _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                routerFlow.navigateTo(Screen.Login)
            }
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

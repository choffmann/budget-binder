package de.hsfl.budgetBinder.presentation.viewmodel.entryViewModel

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import de.hsfl.budgetBinder.presentation.viewmodel.login.LoginTextFieldState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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

    init {
        getCategoryList()
        when (routerFlow.state.value) {
            is Screen.Entry.Overview -> {
                getEntryById((routerFlow.state.value as Screen.Entry.Overview).id)
            }
            is Screen.Entry.Edit -> {
                getEntryById((routerFlow.state.value as Screen.Entry.Edit).id)
            }
            else -> {}
        }
    }

    /* *** Event Handling *** */
    fun onEvent(event: EntryEvent) {
        when (event) {
            is EntryEvent.EnteredName -> _nameText.value = event.value
            is EntryEvent.EnteredAmount -> _amountText.value = event.value
            is EntryEvent.EnteredRepeat -> _repeatState.value = !repeatState.value
            is EntryEvent.EnteredCategoryID -> _categoryIDState.value = event.value
            is EntryEvent.EnteredAmountSign -> _amountSignState.value = !amountSignState.value
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
                    is Screen.Entry.Edit -> updateEntry(
                        Entry.Patch(
                            nameText.value,
                            amountText.value,
                            repeatState.value,
                            Entry.Category(categoryIDState.value)
                        ), selectedEntryState.value.id
                    )
                    else -> routerFlow.navigateTo(Screen.Entry.Edit(selectedEntryState.value.id)) //using ID seems... unnecessary?
                }
            is EntryEvent.OnDeleteEntry -> _dialogState.value = true
            is EntryEvent.OnDeleteDialogConfirm -> deleteEntry(selectedEntryState.value.id)
            is EntryEvent.OnDeleteDialogDismiss -> _dialogState.value = false
            else -> {
                throw Exception("Unhandled EntryEvent in EntryViewModel")
            }
        }

    }


    /* *** Use Case usages *** */
    fun getEntryById(id: Int) {
        entryUseCases.getEntryByIdUseCase(id).onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success<*> -> {
                    _selectedEntryState.value = it.data!!
                }
                is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
            }
        }
    }
    fun getCategoryList() = scope.launch {
        entryUseCases.getCategoryListUseCase.categories()
            .collect { handleDataResponse(response = it, onSuccess = {cl -> _categoryListState.value = cl}) }
    }

    fun createEntry(entry: Entry.In) {
        entryUseCases.createNewEntryUseCase(entry).onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success<*> -> {
                    _eventFlow.emit(UiEvent.ShowSuccess("Entry successfully created")) //TODO?: Change the msg
                    routerFlow.navigateTo(Screen.Dashboard)
                }
                is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
            }
        }
    }

    fun updateEntry(entry: Entry.Patch, id: Int) {
        entryUseCases.changeEntryByIdUseCase(entry, id).onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success<*> -> _eventFlow.emit(UiEvent.ShowSuccess("Entry successfully changed")) //TODO?: Change the msg
                is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
            }
        }
    }

    fun deleteEntry(id: Int) {
        entryUseCases.deleteEntryByIdUseCase(id).onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success<*> -> _eventFlow.emit(UiEvent.ShowSuccess("Entry successfully deleted")) //TODO?: Change the msg
                is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
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
}

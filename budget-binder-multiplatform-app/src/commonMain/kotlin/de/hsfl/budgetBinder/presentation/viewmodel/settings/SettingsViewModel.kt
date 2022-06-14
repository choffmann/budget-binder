package de.hsfl.budgetBinder.presentation.viewmodel.settings

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsUseCases: SettingsUseCases,
    private val dataFlow: DataFlow,
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope
) {

    private val _firstNameText = MutableStateFlow(SettingsTextFieldSate(firstName = dataFlow.userState.value.firstName))
    val firstNameText: StateFlow<SettingsTextFieldSate> = _firstNameText

    private val _lastNameText = MutableStateFlow(SettingsTextFieldSate(lastName = dataFlow.userState.value.name))
    val lastNameText: StateFlow<SettingsTextFieldSate> = _lastNameText

    private val _passwordText = MutableStateFlow(SettingsTextFieldSate(password = "........."))
    val passwordText: StateFlow<SettingsTextFieldSate> = _passwordText

    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.EnteredFirstName -> _firstNameText.value =
                firstNameText.value.copy(firstName = event.value)
            is SettingsEvent.EnteredLastName -> _lastNameText.value = lastNameText.value.copy(firstName = event.value)
            is SettingsEvent.EnteredPassword -> _passwordText.value = passwordText.value.copy(firstName = event.value)
            is SettingsEvent.OnChangeToSettingsUserEdit -> {
                scope.launch {
                    routerFlow.navigateTo(Screen.Settings.User)
                }
            }
            is SettingsEvent.OnChangeToSettingsServerUrlEdit -> {
                scope.launch {
                    routerFlow.navigateTo(Screen.Settings.Server)
                }
            }
            is SettingsEvent.OnDeleteUser -> _dialogState.value = true
            is SettingsEvent.OnLogoutAllDevices -> logOutOnAllDevices()
            is SettingsEvent.OnDeleteDialogConfirm -> {
                _dialogState.value = false
                deleteUser()
            }
            is SettingsEvent.OnDeleteDialogDismiss -> _dialogState.value = false
        }
    }

    private fun logOutOnAllDevices() {
        scope.launch {
            settingsUseCases.logoutUseCase(onAllDevices = true).collect { response ->
                when (response) {
                    is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                    is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                    is DataResponse.Success -> {
                        routerFlow.navigateTo(Screen.Login)
                    }
                    is DataResponse.Unauthorized -> {
                        _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                        routerFlow.navigateTo(Screen.Login)
                    }
                }
            }
        }
    }

    private fun deleteUser() {
        scope.launch {
            settingsUseCases.deleteMyUserUseCase().collect { response ->
                when (response) {
                    is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                    is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                    is DataResponse.Success -> {
                        routerFlow.navigateTo(Screen.Login)
                    }
                    is DataResponse.Unauthorized -> {
                        _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                        routerFlow.navigateTo(Screen.Login)
                    }
                }
            }
        }
    }

    // OLD
    private val _state = MutableStateFlow<UiState>(UiState.Empty)

    @Deprecated(message = "Use events")
    val state: StateFlow<UiState> = _state

    @Deprecated(message = "Use events")
    fun changeMyUser(user: User.Patch) {
        settingsUseCases.changeMyUserUseCase(user).onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Unauthorized -> _state.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }

    @Deprecated(message = "Use events")
    fun deleteMyUser() {
        settingsUseCases.deleteMyUserUseCase().onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Unauthorized -> _state.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }
}

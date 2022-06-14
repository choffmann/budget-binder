package de.hsfl.budgetBinder.presentation.viewmodel.settings

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.login.LoginEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
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

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.EnteredFirstName -> _firstNameText.value =
                firstNameText.value.copy(firstName = event.value)
            is SettingsEvent.EnteredLastName -> _lastNameText.value = lastNameText.value.copy(firstName = event.value)
            is SettingsEvent.EnteredPassword -> _passwordText.value = passwordText.value.copy(firstName = event.value)
            is SettingsEvent.OnChangeToSettingsUserEditClicked -> {
                scope.launch {
                    routerFlow.navigateTo(Screen.Settings.User)
                }
            }
            is SettingsEvent.OnChangeToSettingsServerUrlEditClicked -> {
                scope.launch {
                    routerFlow.navigateTo(Screen.Settings.Server)
                }
            }
            is SettingsEvent.OnLogoutAllDevicesClicked -> {

            }
            is SettingsEvent.OnDeleteDialogConfirm -> {}
            is SettingsEvent.OnDeleteDialogDismiss -> {}
        }
    }

    // OLD
    private val _state = MutableStateFlow<UiState>(UiState.Empty)

    @Deprecated(message = "is Deprecated")
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
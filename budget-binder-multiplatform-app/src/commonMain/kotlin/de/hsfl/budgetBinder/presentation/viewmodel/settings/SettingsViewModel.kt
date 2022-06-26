package de.hsfl.budgetBinder.presentation.viewmodel.settings

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DarkModeFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class SettingsViewModel(
    _settingsUseCases: SettingsUseCases, _routerFlow: RouterFlow, _darkModeFlow: DarkModeFlow, _scope: CoroutineScope
) {
    private val settingsUseCases: SettingsUseCases = _settingsUseCases
    private val routerFlow: RouterFlow = _routerFlow
    private val darkModeFlow: DarkModeFlow = _darkModeFlow
    private val scope: CoroutineScope = _scope

    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState

    private val _darkModeState = MutableStateFlow(settingsUseCases.isDarkThemeUseCase())
    val darkModeState: StateFlow<Boolean> = _darkModeState

    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = UiEventSharedFlow.eventFlow

    init {
        scope.launch {
            darkModeFlow.darkThemeState.collect { _darkModeState.value = it }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnChangeToSettingsUserEdit -> routerFlow.navigateTo(Screen.Settings.User)
            is SettingsEvent.OnChangeToSettingsServerUrlEdit -> routerFlow.navigateTo(Screen.Settings.Server)
            is SettingsEvent.OnDeleteUser -> _dialogState.value = true
            is SettingsEvent.OnLogoutAllDevices -> logOutOnAllDevices()
            is SettingsEvent.OnDeleteDialogConfirm -> {
                _dialogState.value = false
                deleteUser()
            }
            is SettingsEvent.OnDeleteDialogDismiss -> _dialogState.value = false
            is SettingsEvent.OnToggleDarkMode -> {
                scope.launch { settingsUseCases.toggleDarkModeUseCase() }
            }
        }
    }


    fun logOutOnAllDevices(msg: String? = null) = scope.launch {
        settingsUseCases.logoutUseCase(onAllDevices = true).collect { response ->
            response.handleDataResponse<Nothing>(routerFlow = routerFlow, onSuccess = { _ ->
                msg?.let { _eventFlow.emit(UiEvent.ShowSuccess(it)) }
                routerFlow.navigateTo(Screen.Login)
            })
        }
    }


    private fun deleteUser() = scope.launch {
        settingsUseCases.deleteMyUserUseCase().collect { response ->
            response.handleDataResponse<Nothing>(routerFlow = routerFlow, onSuccess = {
                routerFlow.navigateTo(Screen.Login)
            })
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

package de.hsfl.budgetBinder.presentation.viewmodel.navdrawer

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.usecase.LogoutUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class NavDrawerViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob()),
) {

    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = UiEventSharedFlow.eventFlow

    fun onEvent(event: NavDrawerEvent) {
        when (event) {
            is NavDrawerEvent.OnDashboard -> routerFlow.navigateTo(Screen.Dashboard)
            is NavDrawerEvent.OnCreateEntry -> routerFlow.navigateTo(Screen.Entry.Create)
            is NavDrawerEvent.OnCategory -> routerFlow.navigateTo(Screen.Category.Summary)
            is NavDrawerEvent.OnSettings -> routerFlow.navigateTo(Screen.Settings.Menu)
            is NavDrawerEvent.OnLogout -> scope.launch {
                logoutUseCase(onAllDevices = false).collect {
                    when (it) {
                        is DataResponse.Success -> {
                            routerFlow.navigateTo(Screen.Login)
                            _eventFlow.emit(UiEvent.ShowSuccess("Your are logged out"))
                        }
                        is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                        is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                        is DataResponse.Unauthorized -> {
                            routerFlow.navigateTo(Screen.Login)
                            _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                        }
                    }
                }
            }
        }
    }


    // Old
    private val _state = MutableStateFlow<UiState>(UiState.Empty)
    val state: StateFlow<UiState> = _state

    fun logOut(onAllDevices: Boolean) {
        logoutUseCase(onAllDevices).onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(error = it.error!!.message)
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Unauthorized -> _state.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }
}

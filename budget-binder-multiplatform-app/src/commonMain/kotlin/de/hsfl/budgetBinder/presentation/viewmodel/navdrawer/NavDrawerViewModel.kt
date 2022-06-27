package de.hsfl.budgetBinder.presentation.viewmodel.navdrawer

import de.hsfl.budgetBinder.domain.usecase.IsDarkThemeUseCase
import de.hsfl.budgetBinder.domain.usecase.LogoutUseCase
import de.hsfl.budgetBinder.domain.usecase.ToggleDarkModeUseCase
import de.hsfl.budgetBinder.domain.usecase.ToggleServerUrlDialogUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.DarkModeFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NavDrawerViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val toggleServerUrlDialogUseCase: ToggleServerUrlDialogUseCase,
    private val toggleDarkModeUseCase: ToggleDarkModeUseCase,
    private val darkModeFlow: DarkModeFlow,
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope,
    isDarkThemeUseCase: IsDarkThemeUseCase,
) {
    val eventFlow = UiEventSharedFlow.eventFlow

    private val _darkModeState = MutableStateFlow(isDarkThemeUseCase())
    val darkModeState: StateFlow<Boolean> = _darkModeState

    init {
        scope.launch {
            darkModeFlow.mutableDarkThemeState.collect { _darkModeState.value = it }
        }
    }

    fun onEvent(event: NavDrawerEvent) {
        when (event) {
            is NavDrawerEvent.OnChangeServerUrl -> scope.launch { toggleServerUrlDialogUseCase() }
            is NavDrawerEvent.OnToggleDarkMode -> scope.launch { toggleDarkModeUseCase() }
            is NavDrawerEvent.OnDashboard -> routerFlow.navigateTo(Screen.Dashboard)
            is NavDrawerEvent.OnCreateEntry -> routerFlow.navigateTo(Screen.Entry.Create)
            is NavDrawerEvent.OnCategory -> routerFlow.navigateTo(Screen.Category.Summary)
            is NavDrawerEvent.OnSettings -> routerFlow.navigateTo(Screen.Settings.Menu)
            is NavDrawerEvent.OnLogout -> logout()
        }
    }

    private fun logout() = scope.launch {
        logoutUseCase(onAllDevices = false).collect {
            it.handleDataResponse<Nothing>(routerFlow = routerFlow, onSuccess = { routerFlow.navigateTo(Screen.Login) })
        }
    }
}

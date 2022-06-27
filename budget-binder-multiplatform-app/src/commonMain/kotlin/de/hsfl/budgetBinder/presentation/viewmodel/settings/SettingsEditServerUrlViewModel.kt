package de.hsfl.budgetBinder.presentation.viewmodel.settings

import de.hsfl.budgetBinder.domain.usecase.GetServerUrlUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsEditServerUrlViewModel(
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope,
    getServerUrlUseCase: GetServerUrlUseCase
) {
    private val _serverUrl = MutableStateFlow(getServerUrlUseCase())
    val serverUrl: StateFlow<String> = _serverUrl

    fun onEvent(event: EditServerUrlEvent) {
        when (event) {
            is EditServerUrlEvent.GoBack -> {
                scope.launch {
                    routerFlow.navigateTo(Screen.Settings.Menu)
                }
            }
        }
    }
}

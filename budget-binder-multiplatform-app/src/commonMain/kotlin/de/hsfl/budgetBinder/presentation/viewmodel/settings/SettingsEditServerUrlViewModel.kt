package de.hsfl.budgetBinder.presentation.viewmodel.settings

import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsEditServerUrlViewModel(
    private val dataFlow: DataFlow,
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope
) {
    private val _serverUrl = MutableStateFlow(dataFlow.serverUrlState.value.toString())
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

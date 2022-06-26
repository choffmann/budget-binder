package de.hsfl.budgetBinder.presentation.viewmodel

import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.flow.DarkModeFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RootViewModel(
    private val isDarkThemeUseCase: IsDarkThemeUseCase,
    private val storeDarkThemeUseCase: StoreDarkThemeUseCase,
    private val isFirstTimeUseCase: IsFirstTimeUseCase,
    private val toggleDarkModeUseCase: ToggleDarkModeUseCase,
    private val darkModeFlow: DarkModeFlow,
    private val scope: CoroutineScope
) {
    private val _darkModeState = MutableStateFlow(isDarkThemeUseCase())
    val darkModeState: StateFlow<Boolean> = _darkModeState

    init {
        scope.launch {
            darkModeFlow.initDarkMode()
            darkModeFlow.darkThemeState.collect { _darkModeState.value = it }
        }
    }

    fun onEvent(event: RootEvent) {
        when (event) {
            is RootEvent.InitDarkMode -> {
                if (isFirstTimeUseCase() && !isDarkThemeUseCase() && event.isSystemInDarkTheme) {
                    scope.launch { toggleDarkModeUseCase() }
                } else if (isFirstTimeUseCase() && isDarkThemeUseCase()) {
                    storeDarkThemeUseCase(event.isSystemInDarkTheme)
                }
            }
        }
    }
}

sealed class RootEvent {
    data class InitDarkMode(val isSystemInDarkTheme: Boolean) : RootEvent()
}

package de.hsfl.budgetBinder.presentation.flow

import de.hsfl.budgetBinder.domain.usecase.IsFirstTimeUseCase
import de.hsfl.budgetBinder.domain.usecase.NavigateToScreenUseCase
import de.hsfl.budgetBinder.presentation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouterFlow(
    private val navigateToScreenUseCase: NavigateToScreenUseCase,
    private val isFirstTimeUseCase: IsFirstTimeUseCase,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow<Screen>(Screen.Welcome.Screen1)
    val state: StateFlow<Screen> = _state

    init {
        if (isFirstTimeUseCase()) _state.value = Screen.Welcome.Screen1
        else _state.value = Screen.Login
    }

    fun navigateTo(screen: Screen) {
        scope.launch {
            navigateToScreenUseCase(screen).collect {
                _state.value = it
            }
        }
    }
}

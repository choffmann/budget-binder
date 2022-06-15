package de.hsfl.budgetBinder.presentation.flow

import de.hsfl.budgetBinder.domain.usecase.NavigateToScreenUseCase
import de.hsfl.budgetBinder.presentation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RouterFlow(
    private val navigateToScreenUseCase: NavigateToScreenUseCase,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow<Screen>(Screen.Login)
    val state: StateFlow<Screen> = _state

    fun navigateTo(screen: Screen) {
        scope.launch {
            navigateToScreenUseCase(screen).collect {
                _state.value = it
            }
        }
    }
}

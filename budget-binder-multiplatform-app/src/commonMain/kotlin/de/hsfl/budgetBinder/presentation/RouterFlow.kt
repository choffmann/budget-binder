package de.hsfl.budgetBinder.presentation

import de.hsfl.budgetBinder.domain.usecase.NavigateToScreenUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RouterFlow(
    private val navigateToScreenUseCase: NavigateToScreenUseCase
) {
    private val _state = MutableStateFlow<Screen>(Screen.Login)
    val state: StateFlow<Screen> = _state

    suspend fun navigateTo(screen: Screen) {
        navigateToScreenUseCase(screen).collect {
            _state.value = it
        }
    }
}
package de.hsfl.budgetBinder.domain.usecase

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class ToggleDarkModeUseCase(
    private val isDarkThemeUseCase: IsDarkThemeUseCase,
    private val storeDarkThemeUseCase: StoreDarkThemeUseCase
) {
    private var darkThemeMode: Boolean = isDarkThemeUseCase()
    private val _darkThemeState = MutableSharedFlow<Boolean>(replay = 0)
    val darkThemeState: SharedFlow<Boolean> = _darkThemeState

    suspend operator fun invoke() {
        storeDarkThemeUseCase(!darkThemeMode)
        darkThemeMode = isDarkThemeUseCase()
        _darkThemeState.emit(darkThemeMode)
    }
}

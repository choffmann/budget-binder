package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.presentation.flow.DarkModeFlow

class ToggleDarkModeUseCase(
    private val isDarkThemeUseCase: IsDarkThemeUseCase,
    private val storeDarkThemeUseCase: StoreDarkThemeUseCase,
    private val darkModeFlow: DarkModeFlow
) {
    private var darkThemeMode: Boolean = isDarkThemeUseCase()

    suspend operator fun invoke() {
        storeDarkThemeUseCase(!darkThemeMode)
        darkThemeMode = isDarkThemeUseCase()
        darkModeFlow.mutableDarkThemeState.emit(darkThemeMode)
    }
}

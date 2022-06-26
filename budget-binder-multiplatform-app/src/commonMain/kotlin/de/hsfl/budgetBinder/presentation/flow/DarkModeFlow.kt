package de.hsfl.budgetBinder.presentation.flow

import de.hsfl.budgetBinder.domain.usecase.IsDarkThemeUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class DarkModeFlow(private val isDarkThemeUseCase: IsDarkThemeUseCase) {
    val mutableDarkThemeState = MutableSharedFlow<Boolean>(replay = 0)
    val darkThemeState: SharedFlow<Boolean> = mutableDarkThemeState

    suspend fun initDarkMode() {
        mutableDarkThemeState.emit(isDarkThemeUseCase())
    }
}

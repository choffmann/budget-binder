package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.presentation.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NavigateToScreenUseCase {
    operator fun invoke(currentScreen: Screen): Flow<Screen> = flow {
        emit(currentScreen)
    }
}
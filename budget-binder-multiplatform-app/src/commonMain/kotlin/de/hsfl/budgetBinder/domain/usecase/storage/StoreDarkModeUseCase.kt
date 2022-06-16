package de.hsfl.budgetBinder.domain.usecase.storage

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoreDarkModeUseCase {
    operator fun invoke(isDarkMode: Boolean): Flow<Boolean> = flow {
        emit(isDarkMode)
    }
}

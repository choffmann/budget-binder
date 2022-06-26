package de.hsfl.budgetBinder.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AppResetUseCase(
    val resetAllSettings: ResetAllSettings,
    val logoutUseCase: LogoutUseCase,
) {
}

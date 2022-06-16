package de.hsfl.budgetBinder.domain.usecase

data class SettingsUseCases(
    val changeMyUserUseCase: ChangeMyUserUseCase,
    val deleteMyUserUseCase: DeleteMyUserUseCase,
    val logoutUseCase: LogoutUseCase
)

package de.hsfl.budgetBinder.domain.usecase

data class SettingsUseCases(
    @Deprecated("Remove in this data class, is only needed in EditUserViewModel")
    val changeMyUserUseCase: ChangeMyUserUseCase,
    val deleteMyUserUseCase: DeleteMyUserUseCase,
    val logoutUseCase: LogoutUseCase
)

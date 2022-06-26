package de.hsfl.budgetBinder.domain.usecase

data class SettingsUseCases(
    val changeMyUserUseCase: ChangeMyUserUseCase,
    val deleteMyUserUseCase: DeleteMyUserUseCase,
    val logoutUseCase: LogoutUseCase,
    val toggleDarkModeUseCase: ToggleDarkModeUseCase,
    val isDarkThemeUseCase: IsDarkThemeUseCase,
    val resetAllSettings: ResetAllSettings,
)

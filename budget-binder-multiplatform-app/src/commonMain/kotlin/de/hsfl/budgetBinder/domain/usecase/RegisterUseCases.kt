package de.hsfl.budgetBinder.domain.usecase

data class RegisterUseCases(
    val registerUseCase: RegisterUseCase,
    val loginUseCase: LoginUseCase,
    val getMyUserUseCase: GetMyUserUseCase
)

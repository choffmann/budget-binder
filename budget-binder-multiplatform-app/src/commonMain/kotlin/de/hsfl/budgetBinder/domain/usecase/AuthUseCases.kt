package de.hsfl.budgetBinder.domain.usecase

data class AuthUseCases(
    val loginUseCase: LoginUseCase,
    val getMyUserUseCase: GetMyUserUseCase,
    val registerUseCase: RegisterUseCase
)

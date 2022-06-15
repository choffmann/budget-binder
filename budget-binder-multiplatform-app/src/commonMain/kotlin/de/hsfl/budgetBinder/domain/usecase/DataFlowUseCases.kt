package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.domain.usecase.storage.StoreServerUrlUseCase
import de.hsfl.budgetBinder.domain.usecase.storage.StoreUserStateUseCase

data class DataFlowUseCases(
    val storeUserStateUseCase: StoreUserStateUseCase,
    val storeServerUrlUseCase: StoreServerUrlUseCase
)

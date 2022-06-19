package de.hsfl.budgetBinder.domain.usecase

data class DashboardUseCases(
    val getAllEntriesUseCase: GetAllEntriesUseCase,
    val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    val getAllEntriesByCategoryUseCase: GetAllEntriesByCategoryUseCase,
    val deleteEntryByIdUseCase: DeleteEntryByIdUseCase
)

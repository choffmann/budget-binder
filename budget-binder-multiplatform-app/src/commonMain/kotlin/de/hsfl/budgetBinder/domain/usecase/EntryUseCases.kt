package de.hsfl.budgetBinder.domain.usecase

data class EntryUseCases(
    val getAllEntriesUseCase: GetAllEntriesUseCase,
    val getEntryByIdUseCase: GetEntryByIdUseCase,
    val createNewEntryUseCase: CreateNewEntryUseCase,
    val changeEntryByIdUseCase: ChangeEntryByIdUseCase,
    val deleteEntryByIdUseCase: DeleteEntryByIdUseCase
)

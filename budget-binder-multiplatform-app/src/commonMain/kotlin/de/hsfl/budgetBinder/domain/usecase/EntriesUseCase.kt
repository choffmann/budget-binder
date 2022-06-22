package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllEntriesUseCase(private val repository: EntryRepository) {
    operator fun invoke(period: String? = null): Flow<DataResponse<List<Entry>>> = flow {
        useCaseHelper {
            period?.let {
                repository.getAllEntries(it)
            } ?: repository.getAllEntries()
        }
    }
}

class CreateNewEntryUseCase(private val repository: EntryRepository) {
    operator fun invoke(entry: Entry.In): Flow<DataResponse<Entry>> = flow {
        useCaseHelper { repository.createNewEntry(entry) }
    }
}

class GetEntryByIdUseCase(private val repository: EntryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Entry>> = flow {
        useCaseHelper { repository.getEntryById(id) }
    }
}

class ChangeEntryByIdUseCase(private val repository: EntryRepository) {
    operator fun invoke(entry: Entry.Patch, id: Int): Flow<DataResponse<Entry>> = flow {
        useCaseHelper { repository.changeEntryById(entry, id) }
    }
}

class DeleteEntryByIdUseCase(private val repository: EntryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Entry>> = flow {
        useCaseHelper { repository.deleteEntryById(id) }
    }
}

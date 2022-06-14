package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.domain.repository.EntryRepository
import io.ktor.http.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllEntriesUseCase(private val repository: EntryRepository) {
    fun entries(): Flow<DataResponse<List<Entry>>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getAllEntries().let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: response.error?.let { error ->
                    when (error.code) {
                        HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized(error))
                        else -> emit(DataResponse.Error(error))
                    }
                }
            }
        } catch (e: IOException) {
            emit(DataResponse.Error(ErrorModel("Couldn't reach the server")))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(ErrorModel("Somthing went wrong")))
        }
    }

    fun entries(period: String): Flow<DataResponse<List<Entry>>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getAllEntries(period).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: response.error?.let { error ->
                    when (error.code) {
                        HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized(error))
                        else -> emit(DataResponse.Error(error))
                    }
                }
            }
        } catch (e: IOException) {
            emit(DataResponse.Error(ErrorModel("Couldn't reach the server")))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(ErrorModel("Somthing went wrong")))
        }
    }
}

class CreateNewEntryUseCase(private val repository: EntryRepository) {
    operator fun invoke(entry: Entry.In): Flow<DataResponse<Entry>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.createNewEntry(entry).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: response.error?.let { error ->
                    when (error.code) {
                        HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized(error))
                        else -> emit(DataResponse.Error(error))
                    }
                }
            }
        } catch (e: IOException) {
            emit(DataResponse.Error(ErrorModel("Couldn't reach the server")))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(ErrorModel("Somthing went wrong")))
        }
    }
}

class GetEntryByIdUseCase(private val repository: EntryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Entry>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getEntryById(id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: response.error?.let { error ->
                    when (error.code) {
                        HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized(error))
                        else -> emit(DataResponse.Error(error))
                    }
                }
            }
        } catch (e: IOException) {
            emit(DataResponse.Error(ErrorModel("Couldn't reach the server")))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(ErrorModel("Somthing went wrong")))
        }
    }
}

class ChangeEntryByIdUseCase(private val repository: EntryRepository) {
    operator fun invoke(entry: Entry.Patch, id: Int): Flow<DataResponse<Entry>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.changeEntryById(entry, id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: response.error?.let { error ->
                    when (error.code) {
                        HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized(error))
                        else -> emit(DataResponse.Error(error))
                    }
                }
            }
        } catch (e: IOException) {
            emit(DataResponse.Error(ErrorModel("Couldn't reach the server")))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(ErrorModel("Somthing went wrong")))
        }
    }
}

class DeleteEntryByIdUseCase(private val repository: EntryRepository) {
    operator fun invoke(id: Int): Flow<DataResponse<Entry>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.deleteEntryById(id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: response.error?.let { error ->
                    when (error.code) {
                        HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized(error))
                        else -> emit(DataResponse.Error(error))
                    }
                }
            }
        } catch (e: IOException) {
            emit(DataResponse.Error(ErrorModel("Couldn't reach the server")))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(ErrorModel("Somthing went wrong")))
        }
    }
}

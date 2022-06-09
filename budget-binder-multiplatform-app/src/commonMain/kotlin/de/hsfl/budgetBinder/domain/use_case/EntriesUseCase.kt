package de.hsfl.budgetBinder.domain.use_case

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.repository.EntryRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EntriesUseCase(
    private val repository: EntryRepository
) {
    fun getAllEntries(): Flow<DataResponse<List<Entry>>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getAllEntries().let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun getAllEntries(period: String): Flow<DataResponse<List<Entry>>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getAllEntries(period).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun createNewEntry(entry: Entry.In): Flow<DataResponse<Entry>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.createNewEntry(entry).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun getEntryById(id: Int): Flow<DataResponse<Entry>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getEntryById(id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun changeEntryById(entry: Entry.In, id: Int): Flow<DataResponse<Entry>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.changeEntryById(entry, id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }

    fun removeEntryById(id: Int): Flow<DataResponse<Entry>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.removeEntryById(id).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }
}
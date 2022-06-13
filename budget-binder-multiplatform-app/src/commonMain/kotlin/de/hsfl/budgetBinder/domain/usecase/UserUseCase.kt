package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.repository.UserRepository
import io.ktor.http.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMyUserUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<DataResponse<User>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getMyUser().let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: response.error?.let { error ->
                    when (error.code) {
                        HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized())
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

class ChangeMyUserUseCase(private val repository: UserRepository) {
    operator fun invoke(user: User.Patch): Flow<DataResponse<User>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.changeMyUser(user).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: response.error?.let { error ->
                    when (error.code) {
                        HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized())
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

class DeleteMyUserUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<DataResponse<User>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.deleteMyUser().let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: response.error?.let { error ->
                    when (error.code) {
                        HttpStatusCode.Unauthorized.value -> emit(DataResponse.Unauthorized())
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
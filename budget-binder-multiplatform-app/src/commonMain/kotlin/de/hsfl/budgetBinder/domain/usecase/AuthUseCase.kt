package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class RegisterUseCase(private val repository: AuthRepository) {
    operator fun invoke(user: User.In): Flow<DataResponse<User>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.register(user).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Throwable) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }
}

class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<DataResponse<AuthToken>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.authorize(email, password).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Throwable) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }
}

class LogoutUseCase(private val repository: AuthRepository) {
    operator fun invoke(onAllDevices: Boolean): Flow<DataResponse<AuthToken>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.logout(onAllDevices).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error(response.error!!.message))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Throwable) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }
}
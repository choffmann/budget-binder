package de.hsfl.budgetBinder.domain.use_case.auth_user

import de.hsfl.budgetBinder.common.*
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RegisterUseCase(
    private val repository: AuthRepository
) {
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
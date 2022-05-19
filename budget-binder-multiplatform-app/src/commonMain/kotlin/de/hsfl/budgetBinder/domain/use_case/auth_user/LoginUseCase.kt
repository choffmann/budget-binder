package de.hsfl.budgetBinder.domain.use_case.auth_user

import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, password: String): Flow<DataResponse<AuthToken>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.authorize(email, password).let { response ->
                response.data?.let {
                    emit(DataResponse.Success(it))
                } ?: emit(DataResponse.Error("Username or Password incorrect"))
                // Eigentlich response.error!!.message, aber status 401 kommt ohne body
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
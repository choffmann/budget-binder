package de.hsfl.budgetBinder.domain.use_case.auth_user

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(username: String, password: String): Flow<DataResponse<Boolean>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.authorize(username, password)
            emit(DataResponse.Success(true))
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Throwable) {
            emit(DataResponse.Error(e.message.toString()))
        }
    }
}
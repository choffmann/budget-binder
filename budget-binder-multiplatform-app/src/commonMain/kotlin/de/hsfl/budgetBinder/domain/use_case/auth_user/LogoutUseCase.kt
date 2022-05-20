package de.hsfl.budgetBinder.domain.use_case.auth_user

import de.hsfl.budgetBinder.common.*
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LogoutUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(onAllDevices: Boolean): Flow<DataResponse<Boolean>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.logout(onAllDevices)
            emit(DataResponse.Success(true))
            // TODO: Handle Server Error
        } catch (e: IOException) {
            e.printStackTrace()
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Throwable) {
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }
}
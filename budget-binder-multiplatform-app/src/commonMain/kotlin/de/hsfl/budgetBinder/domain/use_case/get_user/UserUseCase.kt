package de.hsfl.budgetBinder.domain.use_case.get_user

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.repository.UserRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<DataResponse<User>> = flow {
        try {
            emit(DataResponse.Loading())
            repository.getMyUser().data?.let { user ->
                println("Response::UserUseCase: $user")
                emit(DataResponse.Success(user))
            }
        } catch (e: IOException) {
            emit(DataResponse.Error("Couldn't reach the server"))
        } catch (e: Exception) {
            println("Response::UserUseCase: error")
            e.printStackTrace()
            emit(DataResponse.Error(e.message.toString()))
        }
    }
}
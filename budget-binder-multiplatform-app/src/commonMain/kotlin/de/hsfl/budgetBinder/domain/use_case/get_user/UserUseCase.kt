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
            repository.getMyUser().let { response ->
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
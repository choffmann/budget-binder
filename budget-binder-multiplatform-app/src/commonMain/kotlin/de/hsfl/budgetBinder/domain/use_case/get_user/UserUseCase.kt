package de.hsfl.budgetBinder.domain.use_case.get_user

import de.hsfl.budgetBinder.common.Resource
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.repository.UserRepository
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.getMyUser()
            emit(Resource.Success(response))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach the server"))
        } catch (e: Throwable) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}
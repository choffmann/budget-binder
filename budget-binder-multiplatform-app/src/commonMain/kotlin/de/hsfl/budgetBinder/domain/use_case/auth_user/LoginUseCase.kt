package de.hsfl.budgetBinder.domain.use_case.auth_user

import de.hsfl.budgetBinder.common.Resource
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import io.ktor.client.plugins.auth.providers.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LoginUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(username: String, password: String): Flow<Resource<BearerTokens>> = flow {
        try {
            emit(Resource.Loading())
            val response = repository.authorize(username, password)
            emit(Resource.Success(response))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach the server"))
        } catch (e: Throwable) {
            emit(Resource.Error(e.message.toString()))
        }
    }
}
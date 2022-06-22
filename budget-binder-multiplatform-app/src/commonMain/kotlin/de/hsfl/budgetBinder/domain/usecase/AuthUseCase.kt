package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import kotlinx.coroutines.flow.*

class RegisterUseCase(private val repository: AuthRepository) {
    operator fun invoke(user: User.In): Flow<DataResponse<User>> = flow {
        useCaseHelper { repository.register(user) }
    }
}

class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<DataResponse<AuthToken>> = flow {
        useCaseHelper { repository.authorize(email, password) }
    }
}

class LogoutUseCase(private val repository: AuthRepository) {
    operator fun invoke(onAllDevices: Boolean): Flow<DataResponse<AuthToken>> = flow {
        useCaseHelper { repository.logout(onAllDevices) }
    }
}

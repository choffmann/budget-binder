package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMyUserUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<DataResponse<User>> = flow {
        useCaseHelper { repository.getMyUser() }
    }
}

class ChangeMyUserUseCase(private val repository: UserRepository) {
    operator fun invoke(user: User.Patch): Flow<DataResponse<User>> = flow {
        useCaseHelper { repository.changeMyUser(user) }
    }
}

class DeleteMyUserUseCase(private val repository: UserRepository) {
    operator fun invoke(): Flow<DataResponse<User>> = flow {
        useCaseHelper { repository.deleteMyUser() }
    }
}

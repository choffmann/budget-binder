package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoreUserStateUseCase {
    operator fun invoke(user: User): Flow<User> = flow {
        emit(user)
    }
}

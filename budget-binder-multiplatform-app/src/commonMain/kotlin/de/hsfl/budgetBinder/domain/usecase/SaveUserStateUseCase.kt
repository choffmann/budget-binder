package de.hsfl.budgetBinder.domain.usecase

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.presentation.Screen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SaveUserStateUseCase {
    operator fun invoke(user: User): Flow<User> = flow {
        emit(user)
    }
}
package de.hsfl.budgetBinder.presentation.flow

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.StoreUserStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DataFlow(
    private val storeUserStateUseCase: StoreUserStateUseCase
) {
    // User data information from backend
    private val _userState = MutableStateFlow(User(0, "", "", ""))
    val userState: StateFlow<User> = _userState

    suspend fun saveUserState(user: User) {
        storeUserStateUseCase(user).collect {
            _userState.value = it
        }
    }
}
package de.hsfl.budgetBinder.presentation.flow

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.StoreUserStateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserFlow(
    private val storeUserStateUseCase: StoreUserStateUseCase,
    private val scope: CoroutineScope
) {
    // User data information from backend
    private val _userState = MutableStateFlow(User(0, "", "", ""))
    val userState: StateFlow<User> = _userState

    fun storeUserState(user: User) = scope.launch {
        storeUserStateUseCase(user).collect {
            _userState.value = it
        }
    }
}

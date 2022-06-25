package de.hsfl.budgetBinder.presentation.flow

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.DataFlowUseCases
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DataFlow(
    private val dataFlowUseCases: DataFlowUseCases,
    private val scope: CoroutineScope
) {
    // User data information from backend
    private val _userState = MutableStateFlow(User(0, "", "", ""))
    val userState: StateFlow<User> = _userState

    fun storeUserState(user: User) = scope.launch {
        dataFlowUseCases.storeUserStateUseCase(user).collect {
            _userState.value = it
        }
    }

    // Dark Mode
    private val _darkModeState = MutableStateFlow(false)
    val darkModeState: StateFlow<Boolean> = _darkModeState

    fun toggleDarkMode() = scope.launch {
        dataFlowUseCases.storeDarkModeUseCase(!darkModeState.value).collect {
            _darkModeState.value = it
        }
    }

}

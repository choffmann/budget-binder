package de.hsfl.budgetBinder.presentation

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UserViewModel(
    private val authUseCase: LoginUseCase,
    private val userUseCase: UserUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {
    private val _state = MutableStateFlow<UserState>(UserState.Empty)
    val state: StateFlow<UserState> = _state

    init {
        auth()
    }

    private fun auth() {
        val username = "root@admin.com"
        val password = "changeme"
        authUseCase(username, password).launchIn(scope)
    }

    fun getMyUser() {
        userUseCase().onEach { result ->
            when (result) {
                is DataResponse.Success -> {
                    _state.value = UserState.Success(user = result.data!!)
                }
                is DataResponse.Error -> {
                    _state.value = UserState.Error(error = result.message ?: "Something went wrong")
                }
                is DataResponse.Loading -> {
                    _state.value = UserState.Loading
                }
            }
        }.launchIn(scope)
    }
}


/*data class UserState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String = ""
)*/

sealed class UserState {
    object Empty : UserState()
    object Loading : UserState()
    data class Success(val user: User) : UserState()
    data class Error(val error: String) : UserState()
}
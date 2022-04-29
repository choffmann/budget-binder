package de.hsfl.budgetBinder.presentation

import de.hsfl.budgetBinder.common.Resource
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import io.ktor.client.plugins.auth.providers.*
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
    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state

    init {
        auth()
    }

    private fun auth() {
        val username = "root@admin.com"
        val password = "changeme"

        authUseCase(username, password).onEach { result ->
            when(result) {
                is Resource.Success -> {
                    _state.value = UserState(auth = result.data)
                }
                is Resource.Error -> {
                    _state.value = UserState(error = result.message ?: "Something went wrong")
                }
                is Resource.Loading -> {
                    _state.value = UserState(isLoading = true)
                }
            }
        }.launchIn(scope)
    }

    fun getMyUser() {
        userUseCase().onEach { result ->
            when(result) {
                is Resource.Success -> {
                    _state.value = UserState(user = result.data)
                }
                is Resource.Error -> {
                    _state.value = UserState(error = result.message ?: "Something went wrong")
                }
                is Resource.Loading -> {
                    _state.value = UserState(isLoading = true)
                }
            }
        }.launchIn(scope)
    }
}



data class UserState(
    val isLoading: Boolean = false,
    val auth: BearerTokens? = null,
    val user: User? = null,
    val error: String = ""
)
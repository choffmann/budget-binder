package de.hsfl.budgetBinder.presentation.viewmodel

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginViewModel(
    private val authUseCase: LoginUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {
    private val _state = MutableStateFlow<LoginState>(LoginState.Empty)
    val state: StateFlow<LoginState> = _state

    fun auth(username: String, password: String) {
        authUseCase(username, password).onEach { auth ->
            when(auth) {
                is DataResponse.Success -> {
                    _state.value = LoginState.Success(true)
                }
                is DataResponse.Error -> {
                    _state.value = LoginState.Error("Username or Password incorrect")
                }
                is DataResponse.Loading -> {
                    _state.value = LoginState.Loading
                }
            }
        }.launchIn(scope)
    }
}

sealed class LoginState {
    object Empty : LoginState()
    object Loading : LoginState()
    data class Success(val login: Boolean) : LoginState()
    data class Error(val error: String) : LoginState()
}
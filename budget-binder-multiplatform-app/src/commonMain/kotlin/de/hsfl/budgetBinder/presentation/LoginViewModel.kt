package de.hsfl.budgetBinder.presentation

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
    private val _state = MutableStateFlow<UiState>(UiState.Empty)
    val state: StateFlow<UiState> = _state

    fun auth(email: String, password: String) {
        authUseCase(email, password).onEach { auth ->
            when (auth) {
                is DataResponse.Success -> {
                    _state.value = UiState.Success(true)
                }
                is DataResponse.Error -> {
                    _state.value = UiState.Error("Username or Password incorrect")
                }
                is DataResponse.Loading -> {
                    _state.value = UiState.Loading
                }
            }
        }.launchIn(scope)
    }
}
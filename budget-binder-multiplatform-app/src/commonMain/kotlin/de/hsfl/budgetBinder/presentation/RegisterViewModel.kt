package de.hsfl.budgetBinder.presentation

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.use_case.auth_user.RegisterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {
    private val _state = MutableStateFlow<UiState>(UiState.Empty)
    val state: StateFlow<UiState> = _state

    fun register(firstName: String, lastName: String, email: String, password: String) {
        registerUseCase(firstName, lastName, email, password).onEach {
            when (it) {
                is DataResponse.Success -> {
                    _state.value = UiState.Success(it.data!!)
                }
                is DataResponse.Error -> {
                    _state.value = UiState.Error(it.message!!)
                }
                is DataResponse.Loading -> {
                    _state.value = UiState.Loading
                }
            }
        }.launchIn(scope)
    }
}
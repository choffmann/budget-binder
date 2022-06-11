package de.hsfl.budgetBinder.presentation.viewmodel

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.GetMyUserUseCase
import de.hsfl.budgetBinder.domain.usecase.LoginUseCase
import de.hsfl.budgetBinder.domain.usecase.RegisterUseCase
import de.hsfl.budgetBinder.presentation.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val getMyUserUseCase: GetMyUserUseCase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {
    private val _state = MutableStateFlow<UiState>(UiState.Empty)
    val state: StateFlow<UiState> = _state

    fun register(user: User.In) {
        registerUseCase(user).onEach {
            when (it) {
                is DataResponse.Success -> login(user.email, user.password)
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }

    private fun login(email: String, password: String) {
        loginUseCase(email, password).onEach {
            when (it) {
                is DataResponse.Success -> getMyUser()
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }

    private fun getMyUser() {
        getMyUserUseCase().onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.message!!)
                is DataResponse.Loading -> _state.value = UiState.Loading
            }
        }.launchIn(scope)
    }
}
package de.hsfl.budgetBinder.presentation.login

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.domain.usecase.LoginUseCases
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCases: LoginUseCases,
    private val routerFlow: RouterFlow,
    private val dataFlow: DataFlow,
    private val scope: CoroutineScope
) {

    private val _emailText = MutableStateFlow(LoginTextFieldState())
    val emailText: StateFlow<LoginTextFieldState> = _emailText

    private val _passwordText = MutableStateFlow(LoginTextFieldState())
    val passwordText: StateFlow<LoginTextFieldState> = _passwordText

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EnteredEmail -> {
                _emailText.value = emailText.value.copy(
                    email = event.value
                )
            }
            is LoginEvent.EnteredPassword -> {
                _passwordText.value = passwordText.value.copy(
                    password = event.value
                )
            }
            is LoginEvent.OnLogin -> {
                auth(email = emailText.value.email, password = passwordText.value.password)
            }
            is LoginEvent.OnChangeToRegister -> {
                scope.launch {
                    routerFlow.navigateTo(Screen.Register)
                }
            }
        }
    }

    private fun auth(email: String, password: String) {
        loginUseCases.loginUseCase(email, password).onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success<*> -> getMyUser()
                else -> _eventFlow.emit(UiEvent.ShowError("Something went wrong"))
            }
        }.launchIn(scope)
    }

    private fun getMyUser() {
        loginUseCases.getMyUserUseCase().onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Success<*> -> {
                    dataFlow.saveUserState(it.data!!)
                    routerFlow.navigateTo(Screen.Dashboard)
                    clearStateFlows()
                }
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                else -> _eventFlow.emit(UiEvent.ShowError("Something went wrong"))
            }
        }.launchIn(scope)
    }

    private fun clearStateFlows() {
        _emailText.value = emailText.value.copy(email = "")
        _passwordText.value = passwordText.value.copy(password = "")
    }

    private val _state = MutableStateFlow<UiState>(UiState.Empty)

    @Deprecated(message = "Old ViewModel, use the new State")
    val state: StateFlow<UiState> = _state

    @Deprecated(message = "Use ViewModel function onEvent()")
    fun login(email: String, password: String) {
        loginUseCases.loginUseCase(email, password).onEach {
            when (it) {
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Success<*> -> getMyUserDeprecated()
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
                is DataResponse.Unauthorized -> _state.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }

    @Deprecated(message = "Use new getMyUser function and SharedFlow UiEvents")
    private fun getMyUserDeprecated() {
        loginUseCases.getMyUserUseCase().onEach {
            when (it) {
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Success<*> -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
                is DataResponse.Unauthorized -> _state.value = UiState.Unauthorized
            }
        }.launchIn(scope)

    }

    sealed class UiEvent {
        object ShowLoading : UiEvent()
        data class ShowError(val msg: String) : UiEvent()
    }
}
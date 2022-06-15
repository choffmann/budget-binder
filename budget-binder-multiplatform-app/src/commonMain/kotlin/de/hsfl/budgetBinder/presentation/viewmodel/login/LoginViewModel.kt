package de.hsfl.budgetBinder.presentation.viewmodel.login

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.utils.validateEmail
import de.hsfl.budgetBinder.domain.usecase.LoginUseCases
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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

    private val _serverUrlText = MutableStateFlow(LoginTextFieldState())
    val serverUrlText: StateFlow<LoginTextFieldState> = _serverUrlText

    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        // try to fetch user '/me' on start. If successful, the user is already authorized
        scope.launch {
            loginUseCases.getMyUserUseCase().collect {
                when (it) {
                    is DataResponse.Success -> {
                        _eventFlow.emit(UiEvent.ShowLoading)
                        dataFlow.storeUserState(it.data!!)
                        delay(1000L)
                        routerFlow.navigateTo(Screen.Dashboard)
                    }
                    else -> {
                        // If the request failed

                        // Debug:
                        // _eventFlow.emit(UiEvent.ShowError("init: user is nor authorized"))
                    }
                }
            }
        }
    }

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EnteredEmail -> _emailText.value =
                emailText.value.copy(email = event.value, emailValid = true)
            is LoginEvent.EnteredPassword -> _passwordText.value =
                passwordText.value.copy(password = event.value)
            is LoginEvent.EnteredServerUrl -> _serverUrlText.value =
                serverUrlText.value.copy(serverAddress = event.value)
            is LoginEvent.OnLogin -> {
                if (validateEmail(email = emailText.value.email)) {
                    toggleDialog()
                } else {
                    _emailText.value = emailText.value.copy(emailValid = false)
                }
            }
            is LoginEvent.OnRegisterScreen -> routerFlow.navigateTo(Screen.Register)
            is LoginEvent.OnServerUrlDialogConfirm -> {
                toggleDialog()
                dataFlow.storeServerUrl(Url(urlString = serverUrlText.value.serverAddress))
                auth(email = emailText.value.email, password = passwordText.value.password)
            }
            is LoginEvent.OnServerUrlDialogDismiss -> toggleDialog()
        }
    }

    private fun toggleDialog() {
        _dialogState.value = !dialogState.value
    }

    private fun auth(email: String, password: String) {
        loginUseCases.loginUseCase(email, password).onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success<*> -> getMyUser()
                is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
            }
        }.launchIn(scope)
    }

    private fun getMyUser() {
        loginUseCases.getMyUserUseCase().onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Success<*> -> {
                    dataFlow.storeUserState(it.data!!)
                    routerFlow.navigateTo(Screen.Dashboard)
                    clearStateFlows()
                }
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                else -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
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
}

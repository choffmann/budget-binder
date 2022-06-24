package de.hsfl.budgetBinder.presentation.viewmodel.auth.login

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.common.utils.validateEmail
import de.hsfl.budgetBinder.domain.usecase.AuthUseCases
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.event.handleLifeCycle
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import de.hsfl.budgetBinder.presentation.viewmodel.auth.AuthViewModel
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authUseCases: AuthUseCases,
    private val routerFlow: RouterFlow,
    private val dataFlow: DataFlow,
    private val scope: CoroutineScope
) : AuthViewModel(
    _scope = scope, _routerFlow = routerFlow, _dataFlow = dataFlow, _authUseCases = authUseCases
) {
    private val screenAfterSuccess = Screen.Dashboard

    private val _emailText = MutableStateFlow(LoginTextFieldState())
    val emailText: StateFlow<LoginTextFieldState> = _emailText

    private val _passwordText = MutableStateFlow(LoginTextFieldState())
    val passwordText: StateFlow<LoginTextFieldState> = _passwordText

    private val _serverUrlText = MutableStateFlow(LoginTextFieldState())
    val serverUrlText: StateFlow<LoginTextFieldState> = _serverUrlText

    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EnteredEmail -> _emailText.value =
                emailText.value.copy(email = event.value, emailValid = true)
            is LoginEvent.EnteredPassword -> _passwordText.value =
                passwordText.value.copy(password = event.value)
            is LoginEvent.EnteredServerUrl -> _serverUrlText.value =
                serverUrlText.value.copy(serverAddress = event.value)
            is LoginEvent.OnLogin -> validateInput { toggleDialog() }
            is LoginEvent.OnRegisterScreen -> routerFlow.navigateTo(Screen.Register)
            is LoginEvent.OnServerUrlDialogConfirm -> validateInput { onServerUrlDialogConfirm() }
            is LoginEvent.OnServerUrlDialogDismiss -> toggleDialog()
            is LoginEvent.LifeCycle -> event.value.handleLifeCycle(onLaunch = { tryToLoginUserOnStart() },
                onDispose = { clearStateFlows() })
        }
    }

    private fun validateInput(actionOnSuccess: () -> Unit) {
        if (validateEmail(email = emailText.value.email)) {
            //TODO: Check what frontend this is opened from.
            // Web -> super.login(...),
            // everyone else -> toggleDialog()
            actionOnSuccess()
        } else {
            _emailText.value = emailText.value.copy(emailValid = false)
        }
    }

    private fun onServerUrlDialogConfirm() {
        toggleDialog()
        dataFlow.storeServerUrl(Url(urlString = serverUrlText.value.serverAddress))
        super.login(email = emailText.value.email, password = passwordText.value.password)
    }

    private fun tryToLoginUserOnStart() = scope.launch {
        authUseCases.getMyUserUseCase().collect {
            it.handleDataResponse<User>(routerFlow = routerFlow, onSuccess = { user ->
                storeUser(user)
                routerFlow.navigateTo(screenAfterSuccess)
            },
                onUnauthorized = { UiEventSharedFlow.mutableEventFlow.emit(UiEvent.HideSuccess) })
        }
    }

    private fun storeUser(user: User) {
        dataFlow.storeUserState(user)
    }

    private fun toggleDialog() {
        _dialogState.value = !dialogState.value
    }

    private fun clearStateFlows() {
        _emailText.value = emailText.value.copy(email = "")
        _passwordText.value = passwordText.value.copy(password = "")
    }
}

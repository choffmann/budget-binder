package de.hsfl.budgetBinder.presentation.viewmodel.auth.register

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.common.utils.validateEmail
import de.hsfl.budgetBinder.domain.usecase.AuthUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.event.handleLifeCycle
import de.hsfl.budgetBinder.presentation.flow.UserFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.auth.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class RegisterViewModel(
    private val authUseCases: AuthUseCases,
    private val routerFlow: RouterFlow,
    scope: CoroutineScope,
    userFlow: UserFlow,
) : AuthViewModel(
    _scope = scope,
    _authUseCases = authUseCases,
    _userFlow = userFlow,
    _routerFlow = routerFlow,
) {
    /*Variables should be refactored ->
    private val _firstNameText = MutableStateFlow(RegisterTextFieldState().firstName)
    val firstNameText: StateFlow<String> = _firstNameText
     */
    private val _firstNameText = MutableStateFlow(RegisterTextFieldState())
    val firstNameText: StateFlow<RegisterTextFieldState> = _firstNameText

    private val _lastNameText = MutableStateFlow(RegisterTextFieldState())
    val lastNameText: StateFlow<RegisterTextFieldState> = _lastNameText

    private val _emailText = MutableStateFlow(RegisterTextFieldState())
    val emailText: StateFlow<RegisterTextFieldState> = _emailText

    private val _passwordText = MutableStateFlow(RegisterTextFieldState())
    val passwordText: StateFlow<RegisterTextFieldState> = _passwordText

    private val _confirmedPasswordText = MutableStateFlow(RegisterTextFieldState())
    val confirmedPasswordText: StateFlow<RegisterTextFieldState> = _confirmedPasswordText

    private val _serverUrlText = MutableStateFlow(RegisterTextFieldState())
    val serverUrlText: StateFlow<RegisterTextFieldState> = _serverUrlText

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EnteredFirstname -> _firstNameText.value =
                firstNameText.value.copy(firstName = event.value)
            is RegisterEvent.EnteredLastname -> _lastNameText.value = lastNameText.value.copy(lastName = event.value)
            is RegisterEvent.EnteredEmail -> _emailText.value =
                emailText.value.copy(email = event.value, emailValid = true)
            is RegisterEvent.EnteredPassword -> _passwordText.value = passwordText.value.copy(password = event.value)
            is RegisterEvent.EnteredConfirmedPassword -> _confirmedPasswordText.value =
                confirmedPasswordText.value.copy(confirmedPassword = event.value, confirmedPasswordValid = true)
            is RegisterEvent.EnteredServerUrl -> _serverUrlText.value =
                serverUrlText.value.copy(serverAddress = event.value)
            is RegisterEvent.OnLoginScreen -> routerFlow.navigateTo(Screen.Login)
            is RegisterEvent.OnRegister -> {
                validateInput {
                    if (!authUseCases.isServerUrlStoredUseCase()) {
                        toggleDialog()
                    } else {
                        super.register(
                            User.In(
                                firstName = firstNameText.value.firstName,
                                name = lastNameText.value.lastName,
                                email = emailText.value.email,
                                password = passwordText.value.password
                            ),
                            serverUrl = serverUrlText.value.serverAddress
                        )
                    }
                }
            }
            is RegisterEvent.LifeCycle -> event.value.handleLifeCycle(
                onLaunch = {},
                onDispose = { resetStateFlows() }
            )
            is RegisterEvent.OnServerUrlDialogConfirm -> validateInput { onServerDialogConfirm() }
            is RegisterEvent.OnServerUrlDialogDismiss -> toggleDialog()
        }
    }

    private fun onServerDialogConfirm() {
        toggleDialog()
        authUseCases.storeServerUrlUseCase(serverUrlText.value.serverAddress)
        super.register(
            User.In(
                firstName = firstNameText.value.firstName,
                name = lastNameText.value.lastName,
                email = emailText.value.email,
                password = passwordText.value.password
            ),
            serverUrl = serverUrlText.value.serverAddress
        )
    }

    private fun validateInput(actionOnSuccess: () -> Unit) {
        val checkEmail =
            if (validateEmail(emailText.value.email)) {
                true
            } else {
                _emailText.value = emailText.value.copy(emailValid = false)
                false
            }

        val checkConfirmedPassword =
            if (passwordText.value.password == confirmedPasswordText.value.confirmedPassword) {
                true
            } else {
                _confirmedPasswordText.value = confirmedPasswordText.value.copy(confirmedPasswordValid = false)
                false
            }

        if (checkEmail && checkConfirmedPassword)
            actionOnSuccess()
    }

    private fun resetStateFlows() {
        _firstNameText.value = firstNameText.value.copy(firstName = "")
        _lastNameText.value = lastNameText.value.copy(lastName = "")
        _emailText.value = emailText.value.copy(email = "")
        _passwordText.value = passwordText.value.copy(password = "")
        _confirmedPasswordText.value = _confirmedPasswordText.value.copy(confirmedPassword = "")
    }
}

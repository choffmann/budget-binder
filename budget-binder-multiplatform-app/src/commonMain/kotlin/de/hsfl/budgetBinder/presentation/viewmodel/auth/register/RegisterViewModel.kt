package de.hsfl.budgetBinder.presentation.viewmodel.auth.register

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.common.utils.validateEmail
import de.hsfl.budgetBinder.domain.usecase.AuthUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.auth.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

class RegisterViewModel(
    private val authUseCases: AuthUseCases,
    private val routerFlow: RouterFlow,
    dataFlow: DataFlow,
    private val scope: CoroutineScope
): AuthViewModel(
    _scope = scope,
    _authUseCases = authUseCases,
    _dataFlow = dataFlow,
    _routerFlow = routerFlow
) {
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
            is RegisterEvent.OnLoginScreen -> routerFlow.navigateTo(Screen.Login)
            is RegisterEvent.OnRegister -> {
                if (validateInput()) {
                    super.register(
                        User.In(
                            firstName = firstNameText.value.firstName,
                            name = lastNameText.value.lastName,
                            email = emailText.value.email,
                            password = passwordText.value.password
                        )
                    )
                }
            }
        }
    }

    private fun validateInput(): Boolean {
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

        return checkEmail && checkConfirmedPassword
    }

    private fun clearStateFlows() {
        _firstNameText.value = firstNameText.value.copy(firstName = "")
        _lastNameText.value = lastNameText.value.copy(lastName = "")
        _emailText.value = emailText.value.copy(email = "")
        _passwordText.value = passwordText.value.copy(password = "")
    }

    // OLD!!!
    private val _state = MutableStateFlow<UiState>(UiState.Empty)

    @Deprecated(message = "Old ViewModel, use the new State")
    val state: StateFlow<UiState> = _state


    @Deprecated(message = "Use ViewModel function onEvent()")
    fun _register(user: User.In) {
        authUseCases.registerUseCase(user).onEach {
            when (it) {
                is DataResponse.Success -> _login(user.email, user.password)
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Unauthorized -> _state.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }

    @Deprecated(message = "This is Deprecated")
    private fun _login(email: String, password: String) {
        authUseCases.loginUseCase(email, password).onEach {
            when (it) {
                is DataResponse.Success -> _getMyUser()
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Unauthorized -> _state.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }

    @Deprecated(message = "This is Deprecated")
    private fun _getMyUser() {
        authUseCases.getMyUserUseCase().onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Unauthorized -> _state.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }
}

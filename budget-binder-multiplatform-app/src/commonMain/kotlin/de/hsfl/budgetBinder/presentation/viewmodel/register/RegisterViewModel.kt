package de.hsfl.budgetBinder.presentation.viewmodel.register

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.common.utils.validateEmail
import de.hsfl.budgetBinder.domain.usecase.RegisterUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCases: RegisterUseCases,
    private val routerFlow: RouterFlow,
    private val dataFlow: DataFlow,
    private val scope: CoroutineScope
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

    private val _eventFlow = UiEventSharedFlow.mutableEventFlow
    val eventFlow = UiEventSharedFlow.eventFlow

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
                    register(
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

    fun register(user: User.In) {
        registerUseCases.registerUseCase(user).onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success<*> -> login(
                    email = emailText.value.email,
                    password = passwordText.value.password
                )
                is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
            }
        }.launchIn(scope)
    }

    private fun login(email: String, password: String) {
        registerUseCases.loginUseCase(email, password).onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                is DataResponse.Success<*> -> getMyUser()
                is DataResponse.Unauthorized -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
            }
        }.launchIn(scope)
    }

    private fun getMyUser() {
        registerUseCases.getMyUserUseCase().onEach {
            when (it) {
                is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                is DataResponse.Success<*> -> {
                    dataFlow.storeUserState(it.data!!)
                    routerFlow.navigateTo(Screen.Dashboard)
                }
                is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
                else -> _eventFlow.emit(UiEvent.ShowError(it.error!!.message))
            }
        }.launchIn(scope)
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
        registerUseCases.registerUseCase(user).onEach {
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
        registerUseCases.loginUseCase(email, password).onEach {
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
        registerUseCases.getMyUserUseCase().onEach {
            when (it) {
                is DataResponse.Success -> _state.value = UiState.Success(it.data)
                is DataResponse.Error -> _state.value = UiState.Error(it.error!!.message)
                is DataResponse.Loading -> _state.value = UiState.Loading
                is DataResponse.Unauthorized -> _state.value = UiState.Unauthorized
            }
        }.launchIn(scope)
    }
}

package de.hsfl.budgetBinder.presentation.viewmodel.settings

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.SettingsUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DarkModeFlow
import de.hsfl.budgetBinder.presentation.flow.UserFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsEditUserViewModel(
    private val settingsUseCases: SettingsUseCases,
    private val userFlow: UserFlow,
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope,
    darkModeFlow: DarkModeFlow
) : SettingsViewModel(
    _settingsUseCases = settingsUseCases, _routerFlow = routerFlow, _scope = scope, _darkModeFlow = darkModeFlow
) {
    /*Variables should be refactored ->
    private val _firstNameText = MutableStateFlow(EditUserState().firstName)
    val firstNameText: StateFlow<String> = _firstNameText
     */
    private val _firstNameText = MutableStateFlow(EditUserState())
    val firstNameText: StateFlow<EditUserState> = _firstNameText

    private val _lastNameText = MutableStateFlow(EditUserState())
    val lastNameText: StateFlow<EditUserState> = _lastNameText

    // Can't access password, so I check if the password placeholder was changed
    private val passwordPlaceholder = ""
    private val _passwordText = MutableStateFlow(EditUserState())
    val passwordText: StateFlow<EditUserState> = _passwordText

    private val _confirmedPasswordText = MutableStateFlow(EditUserState())
    val confirmedPassword: StateFlow<EditUserState> = _confirmedPasswordText

    val emailText: StateFlow<String> = MutableStateFlow(userFlow.userState.value.email)

    private val _eventFlow = UiEventSharedFlow.mutableEventFlow

    init {
        _firstNameText.value =
            firstNameText.value.copy(firstName = userFlow.userState.value.firstName, firstNameIsValid = true)
        _lastNameText.value = lastNameText.value.copy(lastName = userFlow.userState.value.name, lastNameIsValid = true)
        _passwordText.value = passwordText.value.copy(password = passwordPlaceholder, passwordIsValid = true)
        _confirmedPasswordText.value =
            confirmedPassword.value.copy(confirmedPassword = passwordPlaceholder, confirmedPasswordIsValid = true)
    }

    fun onEvent(event: EditUserEvent) {
        when (event) {
            is EditUserEvent.EnteredFirstName -> _firstNameText.value =
                firstNameText.value.copy(firstName = event.value, firstNameIsValid = true)
            is EditUserEvent.EnteredLastName -> _lastNameText.value =
                lastNameText.value.copy(lastName = event.value, lastNameIsValid = true)
            is EditUserEvent.EnteredPassword -> _passwordText.value =
                passwordText.value.copy(password = event.value, passwordIsValid = true)
            is EditUserEvent.EnteredConfirmedPassword -> _confirmedPasswordText.value =
                confirmedPassword.value.copy(confirmedPassword = event.value, confirmedPasswordIsValid = true)
            is EditUserEvent.OnUpdate -> {
                if (checkValidInput()) {
                    // Check if password is changed
                    if (passwordText.value.password == passwordPlaceholder) {
                        updateUser(
                            User.Patch(
                                firstName = firstNameText.value.firstName, name = lastNameText.value.lastName
                            )
                        )
                    } else {
                        updateUser(
                            User.Patch(
                                firstName = firstNameText.value.firstName,
                                name = lastNameText.value.lastName,
                                password = passwordText.value.password
                            )
                        )
                    }
                }
            }
            is EditUserEvent.OnGoBack -> {
                resetFlows()
                routerFlow.navigateTo(Screen.Settings.Menu)
            }
        }
    }

    private fun checkValidInput(): Boolean {
        val checkFirstName = if (firstNameText.value.firstName.isBlank()) {
            _firstNameText.value = firstNameText.value.copy(firstNameIsValid = false)
            false
        } else {
            true
        }

        val checkLastName = if (lastNameText.value.lastName.isBlank()) {
            _lastNameText.value = lastNameText.value.copy(lastNameIsValid = false)
            false
        } else {
            true
        }

        val checkPassword = if (passwordText.value.password.isBlank()) {
            _passwordText.value = passwordText.value.copy(passwordIsValid = false)
            false
        } else {
            true
        }

        val checkConfirmedPassword = if (passwordText.value.password == confirmedPassword.value.confirmedPassword) {
            true
        } else {
            _confirmedPasswordText.value = confirmedPassword.value.copy(confirmedPasswordIsValid = false)
            false
        }
        return checkFirstName && checkLastName && checkPassword && checkConfirmedPassword
    }

    private fun updateUser(user: User.Patch) = scope.launch {
        settingsUseCases.changeMyUserUseCase(user).collect { response ->
            response.handleDataResponse<User>(routerFlow = routerFlow, onSuccess = {
                if (user.password != null) {
                    logOutOnAllDevices("Your password was updated. Please sign in again")
                } else {
                    _eventFlow.emit(UiEvent.ShowSuccess("User update successfully"))
                    userFlow.storeUserState(it)
                    routerFlow.navigateTo(Screen.Settings.Menu)
                }
            })
        }
    }

    private fun resetFlows() {
        _firstNameText.value =
            firstNameText.value.copy(firstName = userFlow.userState.value.firstName, firstNameIsValid = true)
        _lastNameText.value = lastNameText.value.copy(lastName = userFlow.userState.value.name, lastNameIsValid = true)
        _passwordText.value = passwordText.value.copy(password = passwordPlaceholder, passwordIsValid = true)
    }
}

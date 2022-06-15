package de.hsfl.budgetBinder.presentation.viewmodel.settings

import de.hsfl.budgetBinder.common.DataResponse
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.ChangeMyUserUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsEditUserViewModel(
    private val changeMyUserUseCase: ChangeMyUserUseCase,
    private val dataFlow: DataFlow,
    private val routerFlow: RouterFlow,
    private val scope: CoroutineScope
) {
    private val _firstNameText =
        MutableStateFlow(EditUserState(firstName = dataFlow.userState.value.firstName, firstNameIsValid = true))
    val firstNameText: StateFlow<EditUserState> = _firstNameText

    private val _lastNameText =
        MutableStateFlow(EditUserState(lastName = dataFlow.userState.value.name, lastNameIsValid = true))
    val lastNameText: StateFlow<EditUserState> = _lastNameText

    // Can't access password, so I check if the password placeholder was changed
    private val passwordPlaceholder = "........."
    private val _passwordText = MutableStateFlow(EditUserState(password = passwordPlaceholder, passwordIsValid = true))
    val passwordText: StateFlow<EditUserState> = _passwordText

    val emailText: StateFlow<String> = MutableStateFlow(dataFlow.userState.value.email)

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onEvent(event: EditUserEvent) {
        when (event) {
            is EditUserEvent.EnteredFirstName -> _firstNameText.value =
                firstNameText.value.copy(firstName = event.value)
            is EditUserEvent.EnteredLastName -> _lastNameText.value = lastNameText.value.copy(lastName = event.value)
            is EditUserEvent.EnteredPassword -> _passwordText.value = passwordText.value.copy(password = event.value)
            is EditUserEvent.OnUpdate -> {
                if (checkValidInput()) {
                    // Check if password is changed
                    if (passwordText.value.password == passwordPlaceholder) {
                        updateUser(
                            User.Patch(
                                firstName = firstNameText.value.firstName,
                                name = lastNameText.value.lastName
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
            is EditUserEvent.OnGoBack -> routerFlow.navigateTo(Screen.Settings.Menu)
        }
    }

    private fun checkValidInput(): Boolean {
        return if (firstNameText.value.firstName.isBlank()) {
            _firstNameText.value = firstNameText.value.copy(firstNameIsValid = false)
            false
        } else if (lastNameText.value.firstName.isBlank()) {
            _lastNameText.value = lastNameText.value.copy(lastNameIsValid = false)
            false
        } else if (passwordText.value.firstName.isBlank()) {
            _passwordText.value = passwordText.value.copy(lastNameIsValid = false)
            false
        } else {
            true
        }
    }

    private fun updateUser(user: User.Patch) {
        scope.launch {
            changeMyUserUseCase(user).collect { response ->
                when (response) {
                    is DataResponse.Loading -> _eventFlow.emit(UiEvent.ShowLoading)
                    is DataResponse.Error -> _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                    is DataResponse.Success -> {
                        dataFlow.storeUserState(response.data!!)
                        routerFlow.navigateTo(Screen.Settings.Menu)
                    }
                    is DataResponse.Unauthorized -> {
                        _eventFlow.emit(UiEvent.ShowError(response.error!!.message))
                        routerFlow.navigateTo(Screen.Login)
                    }
                }
            }
        }
    }
}

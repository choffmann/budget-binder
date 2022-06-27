package de.hsfl.budgetBinder.presentation.viewmodel.auth

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.AuthUseCases
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.UserFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class AuthViewModel(
    _scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob()),
    _routerFlow: RouterFlow,
    _userFlow: UserFlow,
    _authUseCases: AuthUseCases,
) {
    private val scope = _scope
    private val routerFlow = _routerFlow
    private val userFlow = _userFlow
    private val authUseCases = _authUseCases

    private val _dialogState = MutableStateFlow(false)
    val dialogState: StateFlow<Boolean> = _dialogState

    val eventFlow = UiEventSharedFlow.eventFlow

    init {
        scope.launch {
            authUseCases.toggleServerUrlDialogUseCase.dialogState.collect {
                _dialogState.value = it
            }
        }
    }

    protected fun register(user: User.In, serverUrl: String) = scope.launch {
        authUseCases.registerUseCase(user)
            .collect {
                it.handleDataResponse<User>(
                    routerFlow = routerFlow,
                    onSuccess = { login(email = user.email, password = user.password, serverUrl = serverUrl) })
            }
    }

    protected fun login(email: String, password: String, serverUrl: String) = scope.launch {
        if (!authUseCases.isServerUrlStoredUseCase()) {
            authUseCases.storeServerUrlUseCase(serverUrl)
        }
        authUseCases.loginUseCase(email = email, password = password)
            .collect {
                it.handleDataResponse<Nothing>(routerFlow = routerFlow, onSuccess = {
                    getMyUser()
                })
            }
    }

    private fun getMyUser() = scope.launch {
        authUseCases.getMyUserUseCase().collect {
            it.handleDataResponse<User>(routerFlow = routerFlow, onSuccess = { user ->
                userFlow.storeUserState(user)
                routerFlow.navigateTo(Screen.Dashboard)
            })
        }
    }

    fun toggleDialog() = scope.launch {
        authUseCases.toggleServerUrlDialogUseCase()
    }
}

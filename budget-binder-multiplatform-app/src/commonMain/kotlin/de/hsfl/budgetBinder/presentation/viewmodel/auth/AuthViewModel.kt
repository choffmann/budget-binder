package de.hsfl.budgetBinder.presentation.viewmodel.auth

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.AuthUseCases
import de.hsfl.budgetBinder.domain.usecase.NavigateToScreenUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

open class AuthViewModel(
    _scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob()),
    _routerFlow: RouterFlow = RouterFlow(NavigateToScreenUseCase(), _scope),
    _dataFlow: DataFlow,
    _authUseCases: AuthUseCases
) {
    private val scope = _scope
    private val routerFlow = _routerFlow
    private val dataFlow = _dataFlow
    private val authUseCases = _authUseCases

    val eventFlow = UiEventSharedFlow.eventFlow

    protected fun register(user: User.In) = scope.launch {
        authUseCases.registerUseCase(user)
            .collect { it.handleDataResponse<User>(onSuccess = { login(email = user.email, password = user.password) }) }
    }

    protected fun login(email: String, password: String) = scope.launch {
        authUseCases.loginUseCase(email = email, password = password)
            .collect { it.handleDataResponse<Nothing>(onSuccess = { getMyUser() }) }
    }

    private fun getMyUser() = scope.launch {
        authUseCases.getMyUserUseCase().collect {
            it.handleDataResponse<User>(onSuccess = { user ->
                dataFlow.storeUserState(user)
                routerFlow.navigateTo(Screen.Dashboard)
            })
        }
    }
}

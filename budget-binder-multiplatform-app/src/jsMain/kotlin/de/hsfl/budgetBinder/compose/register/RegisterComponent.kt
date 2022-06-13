package de.hsfl.budgetBinder.compose.register

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.GetMyUserUseCase
import de.hsfl.budgetBinder.domain.usecase.LoginUseCase
import de.hsfl.budgetBinder.domain.usecase.RegisterUseCase

import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.viewmodel.RegisterViewModel
import di
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.instance


@Composable
fun RegisterComponent(screenState: MutableState<Screen>) {
    /*al scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val registerUseCase: RegisterUseCase by di.instance()
    val loginUseCase: LoginUseCase by di.instance()
    val getMyUserUseCase : GetMyUserUseCase by di.instance()
    val viewModel = RegisterViewModel(registerUseCase,loginUseCase,getMyUserUseCase, scope)
    val viewState = viewModel.state.collectAsState(scope)*/
    val scope = rememberCoroutineScope()
    val viewModel: RegisterViewModel by di.instance()
    val viewState = viewModel.state.collectAsState(scope.coroutineContext)


    RegisterView(
        state = viewState,
        onRegisterButtonPressed = { firstName, lastName, email, password ->
            viewModel.register(User.In(firstName,lastName,email,password))
        },
        onChangeToLogin = { screenState.value = Screen.Login }
    )
}
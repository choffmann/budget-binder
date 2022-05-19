package de.hsfl.budgetBinder.compose.login

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.di
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.presentation.LoginViewModel
import de.hsfl.budgetBinder.presentation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.instance

@Composable
fun LoginComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val loginUseCase: LoginUseCase by di.instance()
    val viewModel = LoginViewModel(loginUseCase, scope)
    val viewState = viewModel.state.collectAsState(scope)

    LoginView(
        state = viewState,
        onLoginButtonPressed = { email, password ->
            viewModel.auth(email, password)
        },
        onLoginSuccess = { screenState.value = Screen.User }
    )
}
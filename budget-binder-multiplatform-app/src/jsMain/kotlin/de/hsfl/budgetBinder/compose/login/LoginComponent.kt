package de.hsfl.budgetBinder.compose.login
import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.login.LoginViewModel
import di
import org.kodein.di.compose.withDI
import org.kodein.di.instance


@Composable
fun LoginComponent(screenState: MutableState<Screen>) = withDI(di) {
    /*val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val loginUseCase: LoginUseCase by di.instance()
    val getMyUserUseCase : GetMyUserUseCase by di.instance()
    val viewModel = LoginViewModel(scope,loginUseCase, getMyUserUseCase)
    val viewState = viewModel.state.collectAsState(scope)*/
    val scope = rememberCoroutineScope()
    val viewModel: LoginViewModel by di.instance()
    val viewState = viewModel.state.collectAsState(scope)

    LoginView(
        state = viewState,
        onLoginButtonPressed = { email, password ->
            viewModel.login(email, password)
        },
        onLoginSuccess = { screenState.value = Screen.Dashboard },
        onChangeToRegister = { screenState.value = Screen.Register }
    )
}
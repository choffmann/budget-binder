package de.hsfl.budgetBinder.compose.login
import androidx.compose.runtime.*
import de.hsfl.budgetBinder.domain.usecase.GetMyUserUseCase
import de.hsfl.budgetBinder.domain.usecase.LoginUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.viewmodel.LoginViewModel
import di
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.compose.withDI
import org.kodein.di.instance


@Composable
fun LoginComponent(screenState: MutableState<Screen>) = withDI(di) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val loginUseCase: LoginUseCase by di.instance()
    val getMyUserUseCase : GetMyUserUseCase by di.instance()
    val viewModel = LoginViewModel(scope,loginUseCase, getMyUserUseCase)
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
package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.data.repository.AuthRepositoryImplementation
import de.hsfl.budgetBinder.data.repository.UserRepositoryImplementation
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import de.hsfl.budgetBinder.domain.repository.UserRepository
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import de.hsfl.budgetBinder.presentation.LoginState
import de.hsfl.budgetBinder.presentation.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI
import org.kodein.di.instance

val di = DI {
    bindSingleton { Client() }

    bindSingleton<AuthRepository> { AuthRepositoryImplementation(instance()) }
    bindSingleton<UserRepository> { UserRepositoryImplementation(instance()) }

    bindSingleton { LoginUseCase(instance()) }
    bindSingleton { UserUseCase(instance()) }
}

@Composable
fun App() = withDI(di) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val loginUseCase: LoginUseCase by di.instance()
    val viewModel = LoginViewModel(loginUseCase, scope)
    val screenState = remember { mutableStateOf<Screen>(Screen.Login) }
    val viewState = viewModel.state.collectAsState(scope)

    when (screenState.value) {
        is Screen.Login -> {
            LoginView(
                onLoginButtonPressed = { email, password ->
                    viewModel.auth(email, password)
                    if (viewState.value is LoginState.Success) {
                        screenState.value = Screen.User
                    }
                }, state = viewState
            )
        }
        is Screen.User -> {
            UserView()
        }
    }

    //LoginView()
    //UserView()
}
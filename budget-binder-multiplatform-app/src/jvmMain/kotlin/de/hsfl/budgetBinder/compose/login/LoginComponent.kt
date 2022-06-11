package de.hsfl.budgetBinder.compose.login

import androidx.compose.material.Text
import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.di
import de.hsfl.budgetBinder.presentation.viewmodel.LoginViewModel
import de.hsfl.budgetBinder.presentation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.instance

@Composable
fun LoginComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val viewModel: LoginViewModel by di.instance()
    val viewState = viewModel.state.collectAsState(scope)

    LoginView(
        state = viewState,
        onLoginButtonPressed = { email, password ->
            viewModel.login(email, password)
        },
        onLoginSuccess = { Text(it.toString()) }
    )
}
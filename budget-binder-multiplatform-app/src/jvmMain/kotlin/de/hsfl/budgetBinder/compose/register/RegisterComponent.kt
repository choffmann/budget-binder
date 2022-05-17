package de.hsfl.budgetBinder.compose.register

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.di
import de.hsfl.budgetBinder.domain.use_case.auth_user.RegisterUseCase
import de.hsfl.budgetBinder.presentation.RegisterViewModel
import de.hsfl.budgetBinder.presentation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.instance

@Composable
fun RegisterComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val registerUseCase: RegisterUseCase by di.instance()
    val viewModel = RegisterViewModel(registerUseCase, scope)
    val viewState = viewModel.state.collectAsState(scope)
    RegisterView(
        state = viewState,
        onRegisterButtonPressed = { firstName, lastName, email, password ->
            viewModel.register(firstName, lastName, email, password)
        },
        onRegisterSuccess = { screenState.value = Screen.Login }
    )
}
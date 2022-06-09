package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.domain.use_case.auth_user.LogoutUseCase
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import de.hsfl.budgetBinder.presentation.LogoutViewModel
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun DashboardComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val userUseCase: UserUseCase by di.instance()
    val logoutUseCase: LogoutUseCase by di.instance()
    val userViewModel = UserViewModel(userUseCase, scope)
    val logoutViewModel = LogoutViewModel(logoutUseCase, scope)
    val viewState = userViewModel.state.collectAsState(scope)
    val logOutState = logoutViewModel.state.collectAsState(scope)

    DashboardView(
        state = viewState,
        onUpdate = { userViewModel.getMyUser() },
        onLogout = { logoutViewModel.logOut(false) },
        onCategorySummaryButton = { screenState.value = Screen.CategorySummary},
        onSettingsButton = {screenState.value = Screen.Settings}
    )

    when (logOutState) {
        is UiState.Success<*> -> {
            screenState.value = Screen.Login
        }
    }
}
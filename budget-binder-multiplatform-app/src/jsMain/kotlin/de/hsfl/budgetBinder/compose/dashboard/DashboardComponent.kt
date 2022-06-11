package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import de.hsfl.budgetBinder.presentation.Screen
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
    val userViewModel = UserViewModel(userUseCase, scope)
    val viewState = userViewModel.state.collectAsState(scope)

    DashboardView(
        state = viewState,
        onUpdate = { userViewModel.getMyUser() },
        onCategorySummaryButton = { screenState.value = Screen.CategorySummary},
        onSettingsButton = {screenState.value = Screen.Settings},
        onEntryCreateButton = {screenState.value = Screen.EntryCreate},
        onEntryEditButton = {screenState.value = Screen.EntryEdit}
    )
}
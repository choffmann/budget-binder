package de.hsfl.budgetBinder.compose.categorySummary

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
fun CategorySummaryComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val userUseCase: UserUseCase by di.instance()
    val userViewModel = UserViewModel(userUseCase, scope)
    val viewState = userViewModel.state.collectAsState(scope)

    CategorySummaryView(
        state = viewState,
        onBackButton = { screenState.value = Screen.Dashboard},
        onEditButton = { screenState.value = Screen.Dashboard}
    )
}
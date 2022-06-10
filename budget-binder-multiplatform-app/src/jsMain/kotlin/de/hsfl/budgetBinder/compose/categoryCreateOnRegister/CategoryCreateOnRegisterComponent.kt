package de.hsfl.budgetBinder.compose.categoryCreateOnRegister

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
fun CategoryCreateOnRegisterComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val userUseCase: UserUseCase by di.instance()
    val userViewModel = UserViewModel(userUseCase, scope)
    val viewState = userViewModel.state.collectAsState(scope)

    CategoryCreateOnRegisterView(
        state = viewState,
        onFinishedButton = { screenState.value = Screen.Dashboard} //Should go back to the previous Screen, which could be CategorySummary or EntryCreate.
    )
}
package de.hsfl.budgetBinder.compose.entry

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
fun EntryComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val userUseCase: UserUseCase by di.instance()
    val userViewModel = UserViewModel(userUseCase, scope)
    val viewState = userViewModel.state.collectAsState(scope)

    when (screenState.value) {
        Screen.EntryCreate -> EntryCreateView(
            state = viewState,
            onBackButton = { screenState.value = Screen.Dashboard },
            onCategoryCreateButton = { screenState.value = Screen.CategoryCreate }
        )
        Screen.EntryEdit -> EntryEditView(
            state = viewState,
            onBackButton = { screenState.value = Screen.Dashboard}
        )
        else -> {}
    }
}
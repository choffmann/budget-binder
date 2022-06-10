package de.hsfl.budgetBinder.compose.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.presentation.LoginViewModel
import de.hsfl.budgetBinder.presentation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun CategoryComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val categoryUseCase: LoginUseCase by di.instance() //TODO
    val categoryViewModel = LoginViewModel(categoryUseCase, scope) //TODO
    val viewState = categoryViewModel.state.collectAsState(scope)

    when (screenState.value) {
        Screen.CategoryCreate -> CategoryCreateView(
            state = viewState,
            onBackButton = { screenState.value = Screen.CategorySummary}
        )
        Screen.CategorySummary -> CategorySummaryView(
            state = viewState,
            onBackButton = { screenState.value = Screen.Dashboard},
            onEditButton = { screenState.value = Screen.CategoryEdit},
            onCategoryCreateButton = { screenState.value = Screen.CategoryCreate},
        )
        Screen.CategoryEdit -> CategoryEditView(
            state = viewState,
            onBackButton = { screenState.value = Screen.CategorySummary}
        )
        else -> {}
    }
}
package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.viewmodel.DashboardViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun DashboardComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val getAllEntriesUseCase: GetAllEntriesUseCase by di.instance()
    val getAllCategoriesUseCase: GetAllCategoriesUseCase by di.instance()
    val logoutUseCase : LogoutUseCase by di.instance()
    val dashboardViewModel = DashboardViewModel(getAllEntriesUseCase,getAllCategoriesUseCase,logoutUseCase, scope)
    val categoriesViewState = dashboardViewModel.categoriesState.collectAsState(scope)
    val logoutViewState = dashboardViewModel.logoutState.collectAsState(scope)
    val entriesViewState = dashboardViewModel.entriesState.collectAsState(scope)

    DashboardView(
        categoriesState = categoriesViewState,
        entriesState = entriesViewState,
        logoutState = logoutViewState,
        onCategorySummaryButton = { screenState.value = Screen.CategorySummary},
        onSettingsButton = {screenState.value = Screen.Settings},
        onEntryCreateButton = {screenState.value = Screen.EntryCreate},
        onEntryEditButton = {screenState.value = Screen.EntryEdit}
    )
}
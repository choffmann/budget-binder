package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import di
import org.kodein.di.instance

@Composable
fun DashboardComponent(screenState: MutableState<Screen>) {
    //val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val scope = rememberCoroutineScope()
    /*val di = localDI()
    val getAllEntriesUseCase: GetAllEntriesUseCase by di.instance()
    val getAllCategoriesUseCase: GetAllCategoriesUseCase by di.instance()
    val dashboardViewModel = DashboardViewModel(getAllEntriesUseCase,getAllCategoriesUseCase, scope)
    val categoriesViewState = dashboardViewModel.categoriesState.collectAsState(scope)
    val entriesViewState = dashboardViewModel.entriesState.collectAsState(scope)*/

    val viewModel: DashboardViewModel by di.instance()
    val categoriesViewState = viewModel.categoriesState.collectAsState(scope.coroutineContext)
    val entriesViewState = viewModel.entriesState.collectAsState(scope.coroutineContext)

    DashboardView(
        categoriesState = categoriesViewState,
        entriesState = entriesViewState,
        onCategorySummaryButton = { screenState.value = Screen.CategorySummary},
        onSettingsButton = {screenState.value = Screen._Settings},
        onEntryCreateButton = {categoryList -> screenState.value = Screen.EntryCreate(categoryList)},
        onEntryEditButton = {screenState.value = Screen.EntryEdit}
        onEntryOverviewButton = {id -> screenState.value = Screen.EntryOverview(id)}
    )
}

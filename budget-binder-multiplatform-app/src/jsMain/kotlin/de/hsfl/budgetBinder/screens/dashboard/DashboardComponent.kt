package de.hsfl.budgetBinder.screens.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.*
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEntryState
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardState
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance
import kotlin.math.absoluteValue

@Composable
fun DashboardComponent() {
    val viewModel: DashboardViewModel by di.instance()
    val entryList = viewModel.entryListState.collectAsState()
    val focusedCategory = viewModel.focusedCategoryState.collectAsState()
    val olderEntries = viewModel.oldEntriesMapState.collectAsState()


    //LifeCycle
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(DashboardEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(DashboardEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    //Webpage content
    NavBar {}
    MainFlexContainer {
        Div {
            DashboardData(
                focusedCategory = focusedCategory.value.category,
                totalSpendBudget = focusedCategory.value.spendBudget,
                totalBudget = focusedCategory.value.category.budget,
                hasPrev = focusedCategory.value.hasPrev,
                hasNext = focusedCategory.value.hasNext,
                onPrevClicked = { viewModel.onEvent(DashboardEvent.OnPrevCategory) },
                onNextClicked = { viewModel.onEvent(DashboardEvent.OnNextCategory) }
            )
            EntryList(entryList = entryList.value,
                oldEntries = olderEntries.value,
                onItemClicked = { viewModel.onEvent(DashboardEvent.OnEntry(it)) },
                onLoadMore = { viewModel.onEvent(DashboardEvent.OnLoadMore) },
                onEntryDelete = { viewModel.onEvent(DashboardEvent.OnEntryDelete(it)) }
            )
        }
        CreateNewEntryButton { viewModel.onEvent(DashboardEvent.OnEntryCreate) }
    }
}




package de.hsfl.budgetBinder.screens.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEntryState
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun DashboardComponent() {
    val viewModel: DashboardViewModel by di.instance()
    //val categoryList = viewModel.categoryListSate.collectAsState()
    val entryList = viewModel.entryListState.collectAsState()
    val focusedCategory = viewModel.focusedCategoryState.collectAsState()
    val totalSpendBudget = viewModel.spendBudgetOnCurrentCategory.collectAsState()
    val olderEntries = viewModel.oldEntriesMapState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val loadingState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                else -> loadingState.value = false
            }
        }
    }
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(DashboardEvent.OnEntryCreate) }) {
                Icon(Default.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true
    ) {
        if (loadingState.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Column {
            Row {
                if (focusedCategory.value.hasPrev) {
                    IconButton(onClick = { viewModel.onEvent(DashboardEvent.OnPrevCategory) }) {
                        Icon(Default.ArrowBack, contentDescription = null)
                    }
                }
                Text(focusedCategory.value.category.name)
                if (focusedCategory.value.hasNext) {
                    IconButton(onClick = { viewModel.onEvent(DashboardEvent.OnNextCategory) }) {
                        Icon(Default.ArrowForward, contentDescription = null)
                    }
                }
            }
            Text("Spend: ${totalSpendBudget.value.spendBudgetOnCurrentCategory}")
            Text("Total: ${focusedCategory.value.category.budget}")
            Button(onClick = { viewModel.onEvent(DashboardEvent.OnRefresh) }) {
                Text("Update")
            }
            EntryList(entryList = entryList.value.entryList)
            OlderEntryList(olderEntries.value)
            Button(onClick = { viewModel.onEvent(DashboardEvent.OnLoadMore) }) {
                Text("Load more")
            }
            //EntryList()

        }
    }
}

@Composable
fun FocusedCategory(category: Category) {
    Column { Text(category.name) }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EntryList(entryList: List<DashboardEntryState>) {

    when {
        entryList.isEmpty() -> Text("This category has no entries. You can create an new entry.")
    }
    LazyColumn {
        items(entryList) { state ->
            Divider()
            ListItem(
                text = { Text(state.entry.name) },
                icon = { CategoryImageToIcon(icon = state.categoryImage) },
                trailing = { Text("${state.entry.amount} €") }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun OlderEntryList(entryMap: Map<String, List<Entry>>) {

    when {
        entryMap.isEmpty() -> Text("There are not more entries")
    }
    LazyColumn {
        entryMap.forEach { (date, entries) ->
            stickyHeader {
                Text(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxWidth(), text = date)
            }

            items(entries) { entry ->
                Divider()
                ListItem(
                    text = { Text(entry.name) },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    trailing = { Text("${entry.amount} €") }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun EntryList() {
    data class _DashboardEntryState(
        val entry: Entry,
        val categoryImage: Category.Image = Category.Image.DEFAULT,
        val date: String = "02-2022"
    )

    val dateEntryList = listOf<_DashboardEntryState>(
        _DashboardEntryState(
            entry = Entry(0, "Entry 1", -20.51F, false, null),
            date = "06-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 2", -20.51F, false, null),
            date = "06-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 3", -20.51F, false, null),
            date = "06-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 4", -20.51F, false, null),
            date = "06-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 5", -20.51F, false, null),
            date = "05-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 6", -20.51F, false, null),
            date = "05-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 7", -20.51F, false, null),
            date = "04-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 8", -20.51F, false, null),
            date = "04-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 9", -20.51F, false, null),
            date = "04-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 10", -20.51F, false, null),
            date = "04-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 11", -20.51F, false, null),
            date = "04-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 12", -20.51F, false, null),
            date = "04-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 13", -20.51F, false, null),
            date = "03-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 14", -20.51F, false, null),
            date = "03-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 15", -20.51F, false, null),
            date = "03-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 16", -20.51F, false, null),
            date = "03-2022"
        ),
        _DashboardEntryState(
            entry = Entry(0, "Entry 17", -20.51F, false, null),
            date = "03-2022"
        )
    )

    val groupList = dateEntryList.groupBy { it.date }

    val listState = rememberLazyListState()
    LazyColumn(state = listState) {
        groupList.forEach { (date, states) ->
            stickyHeader {
                Text(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxWidth(), text = date)
            }

            items(states) { state ->
                Divider()
                ListItem(
                    text = { Text(state.entry.name) },
                    icon = { CategoryImageToIcon(icon = state.categoryImage) },
                    trailing = { Text("${state.entry.amount} €") }
                )
            }
        }
    }
}

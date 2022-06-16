package de.hsfl.budgetBinder.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun DashboardComponent() {
    val viewModel: DashboardViewModel by di.instance()
    val categoryList = viewModel.categoryListSate.collectAsState()
    val entryList = viewModel.entryListState.collectAsState()
    val focusedCategory = viewModel.entryListState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val loadingState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                UiEvent.ShowLoading -> loadingState.value = true
                else -> loadingState.value = false
            }
        }
    }

    if(loadingState.value) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(DashboardEvent.OnEntryCreate) }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = true
    ) {
        Column {
            EntryList(entryList = entryList.value.entryList)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EntryList(entryList: List<Entry>) {
    when {
        entryList.isEmpty() -> Text("No entries here, you can create an new entry.")
    }
    LazyColumn {
        items(entryList) { entry ->
            ListItem(
                text = { Text(entry.name) },
                icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                trailing = { Text("${entry.amount} €") }
            )
        }
    }
}

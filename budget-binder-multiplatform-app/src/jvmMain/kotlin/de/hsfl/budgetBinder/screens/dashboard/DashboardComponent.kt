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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DataFlow
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

    if (loadingState.value) {
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
            Row {
                if (focusedCategory.value.hasPrev) {
                    IconButton(onClick = { viewModel.onEvent(DashboardEvent.OnPrevCategory) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
                Text(focusedCategory.value.category.name)
                if (focusedCategory.value.hasNext) {
                    IconButton(onClick = { viewModel.onEvent(DashboardEvent.OnNextCategory) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
            EntryList(entryList = entryList.value.entryList)
        }
    }
}

@Composable
fun FocusedCategory(category: Category) {
    Column { Text(category.name) }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EntryList(entryList: List<Entry>, categoryIcon: Category.Image = Category.Image.DEFAULT) {
    when {
        entryList.isEmpty() -> Text("This category has no entries. You can create an new entry.")
    }
    LazyColumn {
        items(entryList) { entry ->
            ListItem(
                text = { Text(entry.name) },
                icon = { CategoryImageToIcon(icon = categoryIcon) },
                trailing = { Text("${entry.amount} €") }
            )
        }
    }
}

package de.hsfl.budgetBinder.screens.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.compose.BudgetBar
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditEvent
import org.kodein.di.instance

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryDetailView() {
    val viewModel: CategoryDetailViewModel by di.instance()
    val categoryState = viewModel.categoryState.collectAsState()
    val entryListState = viewModel.entryList.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(CategoryDetailEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(CategoryDetailEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    Scaffold(scaffoldState = scaffoldState,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(CategoryDetailEvent.OnEdit) }) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = categoryState.value.name, style = MaterialTheme.typography.h6)
            BudgetBar(progress = 0.5f)
            Text(categoryState.value.toString())
            LazyColumn {
                items(entryListState.value) { entry ->
                    ListItem(text = { Text(entry.name) }, trailing = { Text(entry.amount.toString()) })
                }
            }
            Button(onClick = { viewModel.onEvent(CategoryDetailEvent.OnBack) }) {
                Text("Back")
            }
        }
    }

}

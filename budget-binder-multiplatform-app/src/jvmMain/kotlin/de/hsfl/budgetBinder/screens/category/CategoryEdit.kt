package de.hsfl.budgetBinder.screens.category

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import de.hsfl.budgetBinder.compose.icon.SaveIcon
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditViewModel
import org.kodein.di.instance

@Composable
fun CategoryEditView() {
    val viewModel: CategoryEditViewModel by di.instance()
    val categoryNameState = viewModel.categoryNameState.collectAsState()
    val categoryColorState = viewModel.categoryColorState.collectAsState()
    val categoryImageState = viewModel.categoryImageState.collectAsState()
    val categoryBudgetState = viewModel.categoryBudgetState.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(CategoryEditEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(CategoryEditEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    Scaffold(scaffoldState = scaffoldState,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = {viewModel.onEvent(CategoryEditEvent.OnSave)}) {
                SaveIcon()
            }
        }
    ) {
        Column {
            OutlinedTextField(
                value = categoryNameState.value,
                onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryName(it)) },
                label = { Text("Category Name") }
            )
            OutlinedTextField(
                value = categoryColorState.value,
                onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryColor(it)) },
                label = { Text("Category Color") }
            )
            OutlinedTextField(
                value = categoryBudgetState.value.toString(),
                onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryBudget(it.toFloat())) },
                label = { Text("Category Budget") }
            )
            CategoryImageToIcon(categoryImageState.value)
            Button(onClick = {viewModel.onEvent(CategoryEditEvent.OnCancel)}) {
                Text("Cancel")
            }
        }
    }
}

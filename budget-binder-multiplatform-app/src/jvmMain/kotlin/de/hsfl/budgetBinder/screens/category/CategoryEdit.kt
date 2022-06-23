package de.hsfl.budgetBinder.screens.category

import androidx.compose.material.*
import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.icon.SaveIcon
import de.hsfl.budgetBinder.di
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

    Scaffold(scaffoldState = scaffoldState, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.onEvent(CategoryEditEvent.OnSave) }) {
            SaveIcon()
        }
    }) {
        CategoryFormular(
            categoryNameState = categoryNameState.value,
            categoryColorState = categoryColorState.value.toColor("af"),
            categoryImageState = categoryImageState.value,
            categoryBudgetState = categoryBudgetState.value,
            onEnteredCategoryName = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryName(it)) },
            onEnteredCategoryColor = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryColor(it)) },
            onEnteredCategoryImage = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryImage(it)) },
            onEnteredCategoryBudget = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryBudget(it)) },
            onCancel = { viewModel.onEvent(CategoryEditEvent.OnCancel) }
        )
    }
}



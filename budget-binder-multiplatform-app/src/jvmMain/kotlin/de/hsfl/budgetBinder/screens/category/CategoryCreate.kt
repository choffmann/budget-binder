package de.hsfl.budgetBinder.screens.category

import androidx.compose.material.FabPosition
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import de.hsfl.budgetBinder.compose.icon.SaveIcon
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.create.CategoryCreateEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.create.CategoryCreateViewModel
import org.kodein.di.instance

@Composable
fun CategoryCreateView() {
    val viewModel: CategoryCreateViewModel by di.instance()
    val categoryNameState = viewModel.categoryNameState.collectAsState()
    val categoryColorState = viewModel.categoryColorState.collectAsState()
    val categoryImageState = viewModel.categoryImageState.collectAsState()
    val categoryBudgetState = viewModel.categoryBudgetState.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(CategoryCreateEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(CategoryCreateEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }
    Scaffold(scaffoldState = scaffoldState, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.onEvent(CategoryCreateEvent.OnSave) }) {
            SaveIcon()
        }
    }) {
        CategoryFormular(
            categoryNameState = categoryNameState.value,
            categoryColorState = categoryColorState.value.toColor("af"),
            categoryImageState = categoryImageState.value,
            categoryBudgetState = categoryBudgetState.value,
            onEnteredCategoryName = { viewModel.onEvent(CategoryCreateEvent.EnteredCategoryName(it)) },
            onEnteredCategoryColor = { viewModel.onEvent(CategoryCreateEvent.EnteredCategoryColor(it)) },
            onEnteredCategoryImage = { viewModel.onEvent(CategoryCreateEvent.EnteredCategoryImage(it)) },
            onEnteredCategoryBudget = { viewModel.onEvent(CategoryCreateEvent.EnteredCategoryBudget(it)) },
            onCancel = { viewModel.onEvent(CategoryCreateEvent.OnCancel) }
        )
    }

}

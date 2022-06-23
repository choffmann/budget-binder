package de.hsfl.budgetBinder.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.summary.CategorySummaryEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.summary.CategorySummaryViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun CategorySummary() {
    val viewModel: CategorySummaryViewModel by di.instance()
    val categoryList = viewModel.categoryList.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val loadingState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(CategorySummaryEvent.LifeCycle(LifecycleEvent.OnLaunch))
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                else -> loadingState.value = false
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(CategorySummaryEvent.OnCategoryCreate) }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) {
        if (loadingState.value) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        LazyColumn {
            items(categoryList.value) { category ->
                CategoryListItem(
                    modifier = Modifier.clickable { viewModel.onEvent(CategorySummaryEvent.OnCategory(category.id)) },
                    name = category.name,
                    budget = category.budget.toString(),
                    icon = category.image,
                    color = category.color.toColor("af")
                )
            }
        }
    }
}

fun String.toColor(alpha: String): Color {
    return Color("$alpha$this".toLong(16))
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryListItem(
    modifier: Modifier = Modifier,
    name: String,
    budget: String,
    icon: Category.Image,
    color: Color
) {
    ListItem(
        modifier = modifier,
        text = { Text(name) },
        secondaryText = { Text("Budget: $budget") },
        icon = {
            Box(modifier = Modifier.shadow(15.dp, CircleShape).clip(CircleShape).background(color)) {
                Box(modifier = Modifier.align(Alignment.Center).padding(12.dp)) {
                    CategoryImageToIcon(icon)
                }
            }
        }
    )
}

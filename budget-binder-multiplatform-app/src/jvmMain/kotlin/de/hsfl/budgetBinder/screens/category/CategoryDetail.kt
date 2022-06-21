package de.hsfl.budgetBinder.screens.category

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.BudgetBar
import de.hsfl.budgetBinder.compose.icon.ForwardIcon
import de.hsfl.budgetBinder.compose.icon.ReplyIcon
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditEvent
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun CategoryDetailView() {
    val viewModel: CategoryDetailViewModel by di.instance()
    val categoryState = viewModel.categoryState.collectAsState()
    val entryListState = viewModel.entryList.collectAsState()
    val spendBudgetState = viewModel.budgetOnAllEntries.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val loadingState = remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.onEvent(CategoryDetailEvent.LifeCycle(LifecycleEvent.OnLaunch))
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                else -> loadingState.value = false
            }

        }
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
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            TopCategoryDetailSection(
                totalBudget = categoryState.value.budget,
                totalSpendBudget = spendBudgetState.value,
                category = categoryState.value
            )
            if (loadingState.value) {
                CircularProgressIndicator()
            } else {
                EntryList(entryList = entryListState.value)
                Button(onClick = { viewModel.onEvent(CategoryDetailEvent.OnBack) }) {
                    Text("Back")
                }
            }
        }
    }
}

@Composable
fun TopCategoryDetailSection(
    totalSpendBudget: Float,
    totalBudget: Float,
    category: Category
) {
    Surface(elevation = 8.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = category.name, style = MaterialTheme.typography.h6)
            Spacer(modifier = Modifier.height(8.dp))
            BudgetBar(
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(32.dp).clip(RoundedCornerShape(8.dp)),
                progress = totalSpendBudget / totalBudget,
                color = category.color.toColor("af")
            )
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 250.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                item { Text("Spend: $totalSpendBudget") }
                item {
                    Row {
                        Text("Color: ")
                        Box(modifier = Modifier.clip(CircleShape).size(16.dp).background(category.color.toColor("af")))
                    }
                }
                item { Text("Budget: $totalBudget") }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
private fun EntryList(entryList: List<Entry>) {
    LazyColumn {
        items(
            items = entryList,
            key = { entry -> entry.id }
        ) { entry ->
            ListItem(
                modifier = Modifier.animateItemPlacement(tween(durationMillis = 1000)),
                text = { Text(entry.name) },
                trailing = {
                    if (entry.amount > 0) Text("+${entry.amount}", color = Color.Green)
                    else Text("${entry.amount}", color = Color.Red)
                },
                icon = {
                    if (entry.amount > 0) ForwardIcon()
                    else ReplyIcon()
                }
            )
        }
    }

}

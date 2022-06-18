package de.hsfl.budgetBinder.screens.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
    val entryList = viewModel.entryListState.collectAsState()
    val focusedCategory = viewModel.focusedCategoryState.collectAsState()
    val totalSpendBudget = viewModel.spendBudgetOnCurrentCategory.collectAsState()
    val olderEntries = viewModel.oldEntriesMapState.collectAsState()
    val loadingState = remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.HideSuccess -> loadingState.value = false
                else -> loadingState.value = false
            }
        }
    }


    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onEvent(DashboardEvent.OnEntryCreate) }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) {
        Column {
            if (loadingState.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            TopDashboardSection(
                focusedCategory = focusedCategory.value.category,
                totalSpendBudget = totalSpendBudget.value.spendBudgetOnCurrentCategory,
                totalBudget = focusedCategory.value.category.budget,
                hasPrev = focusedCategory.value.hasPrev,
                hasNext = focusedCategory.value.hasNext,
                onPrevClicked = { viewModel.onEvent(DashboardEvent.OnPrevCategory) },
                onNextClicked = { viewModel.onEvent(DashboardEvent.OnNextCategory) }
            )
            Column {
                EntryList(
                    entryList = entryList.value.entryList,
                    oldEntries = olderEntries.value,
                    onItemClicked = {},
                    onLoadMore = { viewModel.onEvent(DashboardEvent.OnLoadMore) })
                Spacer(modifier = Modifier.height(8.dp))
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun BudgetBar(modifier: Modifier = Modifier, progress: Float) {
    var _progress = progress
    if (progress > 1f) _progress = 1f
    if (progress < 0f) _progress = 0f
    val animatedProgress = animateFloatAsState(
        targetValue = _progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    LinearProgressIndicator(modifier = modifier, progress = animatedProgress)
}

@Composable
private fun TopDashboardSection(
    focusedCategory: Category,
    totalSpendBudget: Float,
    totalBudget: Float,
    hasPrev: Boolean,
    hasNext: Boolean,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    Surface(elevation = 8.dp) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = focusedCategory.name)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                IconButton(
                    modifier = Modifier.weight(1F),
                    onClick = onPrevClicked,
                    enabled = hasPrev
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                }
                BudgetBar(
                    modifier = Modifier.weight(4F).height(32.dp).clip(RoundedCornerShape(8.dp)),
                    progress = totalSpendBudget / totalBudget
                )
                IconButton(
                    modifier = Modifier.weight(1F),
                    onClick = onNextClicked,
                    enabled = hasNext
                ) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
                }
            }
            Text("Spend: $totalSpendBudget")
            Text("Total: $totalBudget")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun EntryList(
    entryList: List<DashboardEntryState>,
    oldEntries: Map<String, List<Entry>>,
    onItemClicked: (Int) -> Unit,
    onLoadMore: () -> Unit
) {

    when {
        entryList.isEmpty() -> Text("This category has no entries. You can create an new entry.")
    }
    LazyColumn {
        items(entryList) { state ->
            Divider()
            ListItem(
                modifier = Modifier.clickable(onClick = { onItemClicked(state.entry.id) }),
                text = { Text(state.entry.name) },
                icon = { CategoryImageToIcon(icon = state.categoryImage) },
                trailing = { Text("${state.entry.amount} €") }
            )
        }
        stickyHeader {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.background(MaterialTheme.colors.background).fillMaxWidth(),
                text = "Older entries..."
            )
        }
        oldEntries.forEach { (date, entries) ->
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

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedButton(onClick = onLoadMore) {
                    Text("Load More")
                }
            }
        }
    }
}


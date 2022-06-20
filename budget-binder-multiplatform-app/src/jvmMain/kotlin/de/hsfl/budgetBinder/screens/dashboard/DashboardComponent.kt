package de.hsfl.budgetBinder.screens.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEntryState
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardState
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
        viewModel.onEvent(DashboardEvent.LifeCycle(LifecycleEvent.OnLaunch))
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.HideSuccess -> loadingState.value = false
                else -> loadingState.value = false
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(DashboardEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }



    Scaffold(scaffoldState = scaffoldState, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.onEvent(DashboardEvent.OnEntryCreate) }) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }) {
        Column {
            if (loadingState.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            TopDashboardSection(focusedCategory = focusedCategory.value.category,
                totalSpendBudget = totalSpendBudget.value.spendBudgetOnCurrentCategory,
                totalBudget = focusedCategory.value.category.budget,
                hasPrev = focusedCategory.value.hasPrev,
                hasNext = focusedCategory.value.hasNext,
                onPrevClicked = { viewModel.onEvent(DashboardEvent.OnPrevCategory) },
                onNextClicked = { viewModel.onEvent(DashboardEvent.OnNextCategory) })
            Column {
                EntryList(entryList = entryList.value.entryList,
                    oldEntries = olderEntries.value,
                    onItemClicked = { viewModel.onEvent(DashboardEvent.OnEntry(it)) },
                    onLoadMore = { viewModel.onEvent(DashboardEvent.OnLoadMore) },
                    onEntryDelete = { viewModel.onEvent(DashboardEvent.OnEntryDelete(it)) }
                )
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
        targetValue = _progress, animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
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
                    modifier = Modifier.weight(1F), onClick = onPrevClicked, enabled = hasPrev
                ) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                }
                BudgetBar(
                    modifier = Modifier.weight(4F).height(32.dp).clip(RoundedCornerShape(8.dp)),
                    progress = totalSpendBudget / totalBudget
                )
                IconButton(
                    modifier = Modifier.weight(1F), onClick = onNextClicked, enabled = hasNext
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
    oldEntries: Map<String, DashboardState>,
    onItemClicked: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onEntryDelete: (Int) -> Unit
) {

    when {
        entryList.isEmpty() -> Text("This category has no entries. You can create an new entry.")
    }
    LazyColumn {
        items(entryList) { state ->
            val swipeState = rememberDismissState()
            if (swipeState.isDismissed(DismissDirection.EndToStart)) {
                onEntryDelete(state.entry.id)
            }
            Divider()
            SwipeToDelete(dismissState = swipeState, content = {
                ListItem(modifier = Modifier.clickable(onClick = { onItemClicked(state.entry.id) }),
                    text = { Text(state.entry.name) },
                    icon = { CategoryImageToIcon(icon = state.categoryImage) },
                    trailing = { Text("${state.entry.amount} €") })
            })
        }
        stickyHeader {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.background(MaterialTheme.colors.background).fillMaxWidth(),
                text = "Older entries..."
            )
        }
        oldEntries.forEach { (date, dashboardState) ->
            stickyHeader {
                Text(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxWidth(), text = date)
            }

            items(dashboardState.entryList) { entryState ->
                val swipeState = rememberDismissState(confirmStateChange = {
                    if (it == DismissValue.DismissedToEnd) {
                        onEntryDelete(entryState.entry.id)
                    }
                    true
                })
                Divider()
                SwipeToDelete(dismissState = swipeState) {
                    ListItem(modifier = Modifier.clickable(onClick = { onItemClicked(entryState.entry.id) }),
                        text = { Text(entryState.entry.name) },
                        icon = { CategoryImageToIcon(icon = entryState.categoryImage) },
                        trailing = { Text("${entryState.entry.amount} €") })
                }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDelete(dismissState: DismissState, content: @Composable RowScope.() -> Unit) {
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        dismissThresholds = { FractionalThreshold(0.2f) },
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    DismissValue.Default -> MaterialTheme.colors.secondary
                    DismissValue.DismissedToStart -> Color.Red
                    else -> MaterialTheme.colors.background
                }
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val scale by animateFloatAsState(targetValue = if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f)
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                if (direction == DismissDirection.EndToStart) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.scale(scale))
                }
            }
        },
        dismissContent = content
    )
}


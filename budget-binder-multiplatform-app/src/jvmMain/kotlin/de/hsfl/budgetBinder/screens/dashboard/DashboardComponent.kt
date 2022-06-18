package de.hsfl.budgetBinder.screens.dashboard

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEntryState
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import org.kodein.di.instance

@Composable
fun DashboardComponent() {
    val viewModel: DashboardViewModel by di.instance()
    val entryList = viewModel.entryListState.collectAsState()
    val focusedCategory = viewModel.focusedCategoryState.collectAsState()
    val totalSpendBudget = viewModel.spendBudgetOnCurrentCategory.collectAsState()
    val olderEntries = viewModel.oldEntriesMapState.collectAsState()
    val loadingState = remember { mutableStateOf(false) }

    Column {
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
            EntryList(entryList = entryList.value.entryList, onItemClicked = {})
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Older entries...", style = MaterialTheme.typography.caption)
            Spacer(modifier = Modifier.height(8.dp))
            OlderEntryList(entryMap = olderEntries.value)
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedButton(onClick = { viewModel.onEvent(DashboardEvent.OnLoadMore) }) {
                    Text("Load More")
                }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EntryList(entryList: List<DashboardEntryState>, onItemClicked: (Int) -> Unit) {

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
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun OlderEntryList(entryMap: Map<String, List<Entry>>) {
    LazyColumn {
        entryMap.forEach { (date, entries) ->
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
    }
}


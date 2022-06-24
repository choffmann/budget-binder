package de.hsfl.budgetBinder.screens.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryViewModel
import de.hsfl.budgetBinder.screens.category.CategoryListItem
import de.hsfl.budgetBinder.screens.category.toColor
import org.kodein.di.instance

@Composable
fun EntryDetailView() {
    val viewModel: EntryViewModel by di.instance()
    val entryState = viewModel.selectedEntryState.collectAsState()
    val categoryList = viewModel.categoryListState.collectAsState()
    val categoryId = viewModel.categoryIDState.collectAsState()
    val entryCategory = categoryList.value.firstOrNull { it.id == categoryId.value }
    val scaffoldState = rememberScaffoldState()
    val defaultCategory =
        Category(id = -1, name = "No Category", image = Category.Image.WRONG, budget = 0f, color = "023047")

    LaunchedEffect(Unit) {
        viewModel.onEvent(EntryEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }

    Scaffold(scaffoldState = scaffoldState, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.onEvent(EntryEvent.OnEditEntry) }) {
            Icon(Icons.Default.Edit, contentDescription = null)
        }
    }) {
        Card(modifier = Modifier.defaultMinSize(minWidth = 200.dp).fillMaxWidth().padding(16.dp), elevation = 15.dp) {
            BoxWithConstraints {
                if (minWidth >= 500.dp) {
                    EntryDetailOnLarge(entry = entryState.value,
                        category = entryCategory ?: defaultCategory,
                        onCancel = { viewModel.onEvent(EntryEvent.OnCancel) })
                } else {
                    EntryDetailOnSmall(entry = entryState.value,
                        category = entryCategory ?: defaultCategory,
                        onCancel = { viewModel.onEvent(EntryEvent.OnCancel) })
                }
            }
        }
    }
}

@Composable
fun EntryDetailOnLarge(
    entry: Entry, category: Category, onCancel: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = entry.name, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EntryAmount(modifier = Modifier.weight(1f), amount = entry.amount)
            EntryCategory(modifier = Modifier.weight(1f), category = category)
            EntryRepeat(modifier = Modifier.weight(1f), repeat = entry.repeat)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCancel) {
            Text("Go Back")
        }
    }
}

@Composable
fun EntryDetailOnSmall(
    entry: Entry, category: Category, onCancel: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(text = entry.name, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EntryAmount(modifier = Modifier.weight(1f), amount = entry.amount)
            EntryRepeat(modifier = Modifier.weight(1f), repeat = entry.repeat)
        }
        EntryCategory(category = category)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCancel) {
            Text("Go Back")
        }
    }
}

@Composable
fun EntryCategory(modifier: Modifier = Modifier, category: Category) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Category", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.shadow(15.dp, CircleShape).clip(CircleShape)
                    .background(category.color.toColor("af"))
            ) {
                Box(modifier = Modifier.align(Alignment.Center).padding(12.dp)) {
                    CategoryImageToIcon(category.image)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = category.name)
        }
    }
}

@Composable
fun EntryAmount(modifier: Modifier = Modifier, amount: Float) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Amount", fontWeight = FontWeight.Bold)
        Text(text = "$amount €")
    }
}

@Composable
fun EntryRepeat(modifier: Modifier = Modifier, repeat: Boolean) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Repeat", fontWeight = FontWeight.Bold)
        Checkbox(checked = repeat, enabled = false, onCheckedChange = {})
    }
}

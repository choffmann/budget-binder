package de.hsfl.budgetBinder.screens.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import de.hsfl.budgetBinder.compose.icon.SaveIcon
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryViewModel
import org.kodein.di.instance

@Composable
fun EntryEditView() {
    val viewModel: EntryViewModel by di.instance()
    val entryName = viewModel.nameText.collectAsState()
    val entryAmount = viewModel.amountText.collectAsState()
    val entryRepeat = viewModel.repeatState.collectAsState()
    val amountSign = viewModel.amountSignState.collectAsState()
    val categoryId = viewModel.categoryIDState.collectAsState()
    val categoryList = viewModel.categoryListState.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(EntryEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(EntryEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    Scaffold(scaffoldState = scaffoldState, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.onEvent(EntryEvent.OnEditEntry) }) {
            SaveIcon()
        }
    }) {
        Column {
            EntryFormular(entryName = entryName.value,
                entryAmount = entryAmount.value,
                entryRepeat = entryRepeat.value,
                entryAmountSign = amountSign.value,
                category = categoryId.value?.let { categoryList.value.first { it.id == categoryId.value } }
                    ?: categoryList.value.first(),
                categoryList = categoryList.value,
                onNameChanged = { viewModel.onEvent(EntryEvent.EnteredName(it)) },
                onAmountChanged = { viewModel.onEvent(EntryEvent.EnteredAmount(it)) },
                onRepeatChanged = { viewModel.onEvent(EntryEvent.EnteredRepeat) },
                onAmountSignChanged = { viewModel.onEvent(EntryEvent.EnteredAmountSign) },
                onCategoryIdChanged = { viewModel.onEvent(EntryEvent.EnteredCategoryID(it)) },
                onCancel = { viewModel.onEvent(EntryEvent.OnCancel) }
            )
        }
    }

}

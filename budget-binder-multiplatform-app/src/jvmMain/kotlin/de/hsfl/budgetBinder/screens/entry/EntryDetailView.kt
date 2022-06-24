package de.hsfl.budgetBinder.screens.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryViewModel
import org.kodein.di.instance

@Composable
fun EntryDetailView() {
    val viewModel: EntryViewModel by di.instance()
    /*val nameState = viewModel.nameText.collectAsState()
    val amountState = viewModel.amountText.collectAsState()
    val repeatState = viewModel.repeatState.collectAsState()*/
    val entryState = viewModel.selectedEntryState.collectAsState()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(EntryEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }

    Scaffold(scaffoldState = scaffoldState, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.onEvent(EntryEvent.OnEditEntry) }) {
            Icon(Icons.Default.Edit, contentDescription = null)
        }
    }) {
        Column {
            Text(entryState.value.toString())
            //Button(onClick = {viewModel.onEvent(EntryEvent.On)})
        }

    }
}

package de.hsfl.budgetBinder.screens.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import org.kodein.di.instance

@Composable
fun EntryComponent() {
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState()
    when (screenState.value) {
        is Screen.Entry.Create -> EntryCreateView()
        is Screen.Entry.Edit -> EntryEditView()
        is Screen.Entry.Overview -> EntryDetailView()
    }
}

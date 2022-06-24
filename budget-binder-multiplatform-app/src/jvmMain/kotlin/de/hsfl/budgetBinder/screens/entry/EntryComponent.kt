package de.hsfl.budgetBinder.screens.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import org.kodein.di.instance

@Composable
fun EntryComponent() {
    val routerFlow: RouterFlow by di.instance()
    when (routerFlow.state.value) {
        is Screen.Entry.Create -> EntryCreateView()
        is Screen.Entry.Edit -> EntryEditView()
        is Screen.Entry.Overview -> EntryDetailView()
    }
}

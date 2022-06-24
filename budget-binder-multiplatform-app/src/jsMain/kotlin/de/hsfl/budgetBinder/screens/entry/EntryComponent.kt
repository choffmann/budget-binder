package de.hsfl.budgetBinder.screens.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.NavBar
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun EntryComponent() {

    val viewModel: EntryViewModel by di.instance()
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState()


    //Webpage content
    NavBar {}
    MainFlexContainer {
        when (screenState.value) {
            is Screen.Entry.Create -> {
                EntryCreateView(
                    onCreateButton = { viewModel.onEvent(EntryEvent.OnCreateEntry) }
                )
            }
            is Screen.Entry.Overview -> {
                EntryOverviewView(
                    onEditButton = { viewModel.onEvent(EntryEvent.OnEditEntry) },
                    onDeleteButton = { viewModel.onEvent(EntryEvent.OnDeleteEntry) },
                    onDeleteDialogConfirmButton = { viewModel.onEvent(EntryEvent.OnDeleteDialogConfirm) },
                    onDeleteDialogDismissButton = { viewModel.onEvent(EntryEvent.OnDeleteDialogDismiss) },
                    onCancel = { viewModel.onEvent(EntryEvent.OnCancel) }
                )
            }
            is Screen.Entry.Edit -> {
                EntryEditView(
                    onEditButton = { viewModel.onEvent(EntryEvent.OnEditEntry) }
                )
            }
            else -> {
            }
        }
    }
}

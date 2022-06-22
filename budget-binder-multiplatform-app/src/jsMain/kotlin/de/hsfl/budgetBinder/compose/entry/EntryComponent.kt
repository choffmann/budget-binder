package de.hsfl.budgetBinder.compose.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.NavBar
import de.hsfl.budgetBinder.compose.category.categoryIdToCategory
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow

import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.entryViewModel.EntryViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.kodein.di.instance
import kotlin.math.absoluteValue

@Composable
fun EntryComponent() {

    val viewModel: EntryViewModel by di.instance()
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState()
    val loadingState = remember { mutableStateOf(false) }
    //Data to load
    val entry = viewModel.selectedEntryState.collectAsState()
    val categoryList = viewModel.categoryListState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.HideSuccess -> loadingState.value = false
                else -> loadingState.value = false
            }
        }
    }
    NavBar {}
    MainFlexContainer {
        when (screenState.value) {
            is Screen.EntryCreate -> {
                EntryCreateView(
                    categoryList = (screenState.value as Screen.EntryCreate).categoryList,
                    onCreateEntryButtonPressed = { name: String, amount: Float, repeat: Boolean, category_id: Int ->
                        viewModel.createEntry(Entry.In(name, amount, repeat, category_id))
                    }
                )

            }
            is Screen.EntryEdit -> {
                EntryEditView(
                    onEditEntryButtonPressed = { name: String, amount: Float, repeat: Boolean, category: Entry.Category? ->
                        viewModel.changeEntry(
                            Entry.Patch(name, amount, repeat, category),
                            (screenState.value as Screen.EntryEdit).id
                        )
                    }
                )
                viewModel.getEntryById((screenState.value as Screen.EntryEdit).id)//(screenState.value as Screen.EntryEdit).id)
            }
            is Screen.EntryOverview -> {
                EntryOverviewView(
                    onEditButton = { id, -> screenState.value = Screen.EntryEdit(id) },
                    onDeleteButton = { id ->
                        viewModel.removeEntry(id)
                        screenState.value = Screen.Dashboard
                    }
                )
                viewModel.getEntryById((screenState.value as Screen.EntryOverview).id)
            }
            else -> {}
        }
    }
}

fun entriesFromCategory(list: List<Entry>, category_id: Int?): List<Entry> =
    list.filter { it.category_id == category_id }

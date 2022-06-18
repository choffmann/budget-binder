package de.hsfl.budgetBinder.compose.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.category.categoryIdToCategory
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.viewmodel.EntryViewModel
import di
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.kodein.di.compose.localDI
import org.kodein.di.instance
import kotlin.math.absoluteValue

@Composable
fun EntryComponent(screenState: MutableState<Screen>) {
    //val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val scope = rememberCoroutineScope()
    /*val di = localDI()
    val getAllEntriesUseCase: GetAllEntriesUseCase by di.instance()
    val getEntryByIdUseCase: GetEntryByIdUseCase by di.instance()
    val changeEntryByIdUseCase: ChangeEntryByIdUseCase by di.instance()
    val deleteEntryByIdUseCase: DeleteEntryByIdUseCase by di.instance()
    val createNewEntryUseCase: CreateNewEntryUseCase by di.instance()
    val userViewModel = EntryViewModel(getAllEntriesUseCase, getEntryByIdUseCase, createNewEntryUseCase,changeEntryByIdUseCase,deleteEntryByIdUseCase, scope)
    val viewState = userViewModel.state.collectAsState(scope)*/

    val viewModel: EntryViewModel by di.instance()
    val viewState = viewModel.state.collectAsState(scope.coroutineContext)

    when (screenState.value) {
        is Screen.EntryCreate -> {EntryCreateView(
            state = viewState,
            categoryList = (screenState.value as Screen.EntryCreate).categoryList,
            onChangeToDashboard = { screenState.value = Screen.Dashboard },
            onCreateEntryButtonPressed = { name, amount, repeat, category_id ->
                userViewModel.createEntry(Entry.In(name, amount, repeat, category_id)) },
            onChangeToSettings = { screenState.value = Screen.Settings },
            onChangeToCategory = { screenState.value = Screen.CategorySummary },
        )

        }
        is Screen.EntryEdit -> EntryEditView(
            state = viewState,
            onBackButton = { screenState.value = Screen.Dashboard }
        )
        is Screen.EntryOverview -> {
            EntryOverviewView(
                state = viewState,
                onEditButton = {},
                onDeleteButton = { id -> entryViewModel.removeEntry(id) },
                onChangeToDashboard = { screenState.value = Screen.Dashboard },
                onChangeToSettings = { screenState.value = Screen.Settings },
                onChangeToCategory = { screenState.value = Screen.CategorySummary },
            )
            entryViewModel.getEntryById((screenState.value as Screen.EntryOverview).id)
        }
        else -> {}
    }
}

//Should be put in own File
@Composable
fun EntryListElement(entry: Entry, categoryList: List<Category>, onEntry: (id: Int) -> Unit) {
    Div(attrs = {
        classes("mdc-card", "mdc-card--outlined", AppStylesheet.entryListElement)
        onClick { onEntry(entry.id) }
    }) {
        CategoryImageToIcon(categoryIdToCategory(entry.category_id, categoryList).image)
        Div(attrs = { classes(AppStylesheet.entryListElementText) }) {
            Div(attrs = {
                classes(
                    "mdc-typography--headline5",
                    AppStylesheet.text
                )
            }) { Text(entry.name) }
        }
        Div(attrs = { classes(AppStylesheet.imageFlexContainer) }) {
            Div(attrs = {
                classes(
                    "mdc-typography--headline5",
                    AppStylesheet.moneyText
                )
            }) { Text(amountToString(entry.amount)) }
        }
    }
}

fun amountToString(amount: Float): String {
    //This whole thing just so it's "- 10 €" and not "-10 €"
    val x = if (amount < 0) "-" else ""
    return "$x ${amount.absoluteValue} €"
}

@Composable
fun EntryList(list: List<Entry>, categoryList: List<Category>, onEntry: (id: Int) -> Unit) {
    if (list.isEmpty()) {
        Div(attrs = {
            classes(
                "mdc-typography--headline5",
                AppStylesheet.text
            )
        }) { Text("No Entries in this category") }
    } else {
        for (entry in list) {
            EntryListElement(entry, categoryList, onEntry)
        }
    }
}

fun entriesFromCategory(list: List<Entry>, category_id: Int?): List<Entry> =
    list.filter { it.category_id == category_id }

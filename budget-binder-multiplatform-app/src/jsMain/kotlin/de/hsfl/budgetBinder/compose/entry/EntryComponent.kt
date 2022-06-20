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
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
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
        is Screen.EntryCreate -> {
            EntryCreateView(
                state = viewState,
                categoryList = (screenState.value as Screen.EntryCreate).categoryList,
                onChangeToDashboard = { screenState.value = Screen.Dashboard },
                onCreateEntryButtonPressed = { name:String, amount:Float, repeat:Boolean, category_id:Int ->
                    viewModel.createEntry(Entry.In(name, amount, repeat, category_id))
                },
                onChangeToSettings = { screenState.value = Screen._Settings },
                onChangeToCategory = { screenState.value = Screen.CategorySummary },
            )

        }
        is Screen.EntryEdit -> {
            EntryEditView(
                state = viewState,
                onChangeToDashboard = { screenState.value = Screen.Dashboard },
                onEditEntryButtonPressed = { name:String, amount:Float, repeat:Boolean, category:Entry.Category? ->
                    viewModel.changeEntry(
                        Entry.Patch(name, amount, repeat, category),
                        (screenState.value as Screen.EntryEdit).id
                    )
                },
                onChangeToSettings = { screenState.value = Screen._Settings },
                onChangeToCategory = { screenState.value = Screen.CategorySummary },
            )
            viewModel.getEntryById((screenState.value as Screen.EntryEdit).id)//(screenState.value as Screen.EntryEdit).id)
        }
        is Screen.EntryOverview -> {
            EntryOverviewView(
                state = viewState,
                onEditButton = {id, -> screenState.value = Screen.EntryEdit(id)},
                onDeleteButton = { id ->
                    viewModel.removeEntry(id)
                    screenState.value = Screen.Dashboard},
                onChangeToDashboard = { screenState.value = Screen.Dashboard },
                onChangeToSettings = { screenState.value = Screen._Settings },
                onChangeToCategory = { screenState.value = Screen.CategorySummary },
            )
            viewModel.getEntryById((screenState.value as Screen.EntryOverview).id)
        }
        else -> {}
    }
}

fun entriesFromCategory(list: List<Entry>, category_id: Int?): List<Entry> =
    list.filter { it.category_id == category_id }

package de.hsfl.budgetBinder.compose.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.category.categoryIdToCategory
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
        Screen.EntryCreate -> EntryCreateView(
            state = viewState,
            onBackButton = { screenState.value = Screen.Dashboard },
            onCategoryCreateButton = { screenState.value = Screen.CategoryCreate }
        )
        Screen.EntryEdit -> EntryEditView(
            state = viewState,
            onBackButton = { screenState.value = Screen.Dashboard}
        )
        else -> {}
    }
}

//Should be put in own File
@Composable
fun EntryListElement(entry: Entry, categoryList : List<Category>){
    Div {
        CategoryImageToIcon(categoryIdToCategory(entry.category_id,categoryList).image)
        Text(entry.name)
        Text(entry.amount.toString()+"€")

    }
}

@Composable
fun EntryList(list: List<Entry>, categoryList : List<Category>){
    for (entry in list){
        EntryListElement(entry,categoryList)
    }
}

fun entriesFromCategory(list: List<Entry>, category_id: Int?):List<Entry> =
    list.filter { it.category_id == category_id }
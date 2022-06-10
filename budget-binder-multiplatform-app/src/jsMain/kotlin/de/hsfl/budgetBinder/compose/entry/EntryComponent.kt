package de.hsfl.budgetBinder.compose.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun EntryComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val userUseCase: UserUseCase by di.instance()
    val userViewModel = UserViewModel(userUseCase, scope)
    val viewState = userViewModel.state.collectAsState(scope)

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
fun EntryListElement(entry: Entry){
    Div {
        //TODO: Icon der Kategorie hier anzeigen: Category.Icon(entry.category_id)
        Text(entry.name)
        Text(entry.amount.toString()+"€")
    }
}

@Composable
fun EntryList(list: List<Entry>){
    for (entry in list){
        EntryListElement(entry)
    }
}
package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.category.Bar
import de.hsfl.budgetBinder.compose.entry.EntryList
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import kotlin.math.log


@Composable
fun DashboardView(
    categoriesState: State<Any>,
    entriesState: State<Any>,
    onCategorySummaryButton: () -> Unit,
    onSettingsButton: () -> Unit,
    onEntryCreateButton: () -> Unit,
    onEntryEditButton: () -> Unit
) {
    val categoriesViewState by remember { categoriesState }
    val entriesViewState by remember { entriesState }
    var categoryList by remember { mutableStateOf<List<Category>>(emptyList()) }

    var entryList by remember { mutableStateOf<List<Entry>>(emptyList()) }

    Div {
        H1 { Text("DashboardView") }
        Button(attrs = {
            onClick { onSettingsButton() }
        }) {
            Text("Open Settings")
        }
        Button(attrs = {
            onClick { onCategorySummaryButton() }
        }) {
            Text("Open Category List (Summary of every Category)")
        }
        Button(attrs = {
            onClick { onEntryCreateButton() }
        }) {
            Text("Create Entry")
        }
        Button(attrs = {
            onClick { onEntryEditButton() }
        }) {
            Text("Edit Entry (Needs to be there for every Entry shown)")
        }
    }
    Div {
        UpdateDashboardData(categoryList, entryList)
    }
    //Process new Category Data
    when (categoriesViewState) {
        is UiState.Success<*> -> {
            //Updates Data
            // https://stackoverflow.com/questions/36569421/kotlin-how-to-work-with-list-casts-unchecked-cast-kotlin-collections-listkot
            when (val element = (categoriesViewState as UiState.Success<*>).element) {
                is List<*> -> {
                    element.filterIsInstance<Category>()
                        .let {
                            if (it.size == element.size) {
                                categoryList = it
                            }
                        }
                }
            }
        }
        is UiState.Error -> {
            Text((categoriesViewState as UiState.Error).error)
        }
        is UiState.Loading -> {
            //CircularProgressIndicator()
        }
    }
    //Process new Entry Data
    when (entriesViewState) {
        is UiState.Success<*> -> {
            //Updates Data
            when (val element = (entriesViewState as UiState.Success<*>).element) {
                is List<*> -> {
                    element.filterIsInstance<Entry>()
                        .let {
                            if (it.size == element.size) {
                                entryList = it
                            }
                        }
                }
            }

        }
        is UiState.Error -> {
            Text((entriesViewState as UiState.Error).error)
        }
        is UiState.Loading -> {
            //CircularProgressIndicator()
        }
    }
}

@Composable
fun UpdateDashboardData(categoryList: List<Category>, entryList: List<Entry>) {
    console.log("Category $categoryList and Entry $entryList")
    if (categoryList.isNotEmpty() && entryList.isNotEmpty()) {
        Bar(categoryList[0], entryList) //Bar for first Category, needs to be changed later
        EntryList(entryList, categoryList)
    }
}

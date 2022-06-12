package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.category.Bar
import de.hsfl.budgetBinder.compose.category.Icon
import de.hsfl.budgetBinder.compose.entry.EntryList
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text


@Composable
fun DashboardView(
    state: State<Any>,
    onCategorySummaryButton: () -> Unit,
    onSettingsButton: () -> Unit,
    onEntryCreateButton: () -> Unit,
    onEntryEditButton: () -> Unit
) {
    val viewState by remember { state }
    var categoryList : List<Category> = emptyList()
    var entryList : List<Entry> = emptyList()

    Div {
        when (viewState) {
            is UiState.Success<*> -> {
                val element = (viewState as UiState.Success<*>).element
                @Suppress("UNCHECKED_CAST") // https://stackoverflow.com/questions/36569421/kotlin-how-to-work-with-list-casts-unchecked-cast-kotlin-collections-listkot
                categoryList = element as? List<Category> ?: categoryList
                @Suppress("UNCHECKED_CAST")
                entryList = element as? List<Entry> ?: entryList
            }
            is UiState.Error -> {
                Text((viewState as UiState.Error).error)
            }
            is UiState.Loading -> {
                //CircularProgressIndicator()
            }
        }
        H1{Text("DashboardView")}
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
        /*val category = Category(0,"","FFFFFF",Category.Image.SHOPPING, 12f)
        val entryList2 = listOf(
            Entry(0,"Flowers",10f,false,0),
            Entry(1,"Food",12f,false,0)
        )*/

        Icon(categoryList[0])
        Bar(categoryList[0],entryList)
        EntryList(entryList)
    }
}

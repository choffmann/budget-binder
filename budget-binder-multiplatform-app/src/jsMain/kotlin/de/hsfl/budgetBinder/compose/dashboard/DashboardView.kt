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
    onUpdate: () -> Unit,
    onCategorySummaryButton: () -> Unit,
    onSettingsButton: () -> Unit,
    onEntryCreateButton: () -> Unit,
    onEntryEditButton: () -> Unit
) {
    val viewState by remember { state }


    H1{Text("DashboardView")}
    Div {
        when (viewState) {
            is UiState.Success<*> -> {
                Text((viewState as UiState.Success<*>).element.toString())
            }
            is UiState.Error -> {
                Text((viewState as UiState.Error).error)
            }
            is UiState.Loading -> {
                //CircularProgressIndicator()
            }
        }
        Button(attrs = {
            onClick { onUpdate() }
        }) {
            Text("Update")
        }
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
        val category = Category(0,"","FFFFFF",Category.Image.SHOPPING, 12f)
        val entryList = listOf(
            Entry(0,"Flowers",10f,false,0),
            Entry(1,"Food",12f,false,0)
        )
        Icon(category)
        Bar(category,entryList)
        EntryList(entryList)
    }
}

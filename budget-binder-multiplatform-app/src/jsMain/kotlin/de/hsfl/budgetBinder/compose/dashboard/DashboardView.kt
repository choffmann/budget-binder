package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text


@Composable
fun DashboardView(
    state: State<Any>,
    onUpdate: () -> Unit,
    onLogout: () -> Unit,
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
            onClick { onLogout() }
        }) {
            Text("LogOut")
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
    }
}

package de.hsfl.budgetBinder.compose.settings

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text


@Composable
fun SettingsView(
    state: State<Any>,
    onBackButton: () -> Unit
) {
    val viewState by remember { state }
    H1{Text("SettingsView")}
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
            onClick { onBackButton() }
        }) {
            Text("Back to Dashboard")
        }
    }
}
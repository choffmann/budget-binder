package de.hsfl.budgetBinder.compose.entryCreate

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text


@Composable
fun EntryCreateView(
    state: State<Any>,
    onBackButton: () -> Unit,
    onCategoryCreateButton: () -> Unit
) {
    val viewState by remember { state }
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
        Button(attrs = {
            onClick { onCategoryCreateButton() }
        }) {
            Text("Create new Category (Needs to be put as the last option when selecting a category for an entry)")
        }
    }
}

package de.hsfl.budgetBinder.compose.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text


@Composable
fun CategoryCreateOnRegisterView(
    state: State<Any>,
    onFinishedButton: () -> Unit
) {
    val viewState by remember { state }
    H1{Text("CategoryCreateOnRegisterView")}
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
            onClick { onFinishedButton() }
        }) {
            Text("Proceed to Dashboard")
        }
    }
}

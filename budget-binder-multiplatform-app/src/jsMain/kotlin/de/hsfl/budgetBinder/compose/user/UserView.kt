package de.hsfl.budgetBinder.compose.user

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text


@Composable
fun UserView(
    state: State<Any>,
    onUpdate: () -> Unit,
    onLogout: () -> Unit
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
            onClick { onUpdate() }
        }) {
            Text("Update")
        }
        Button(attrs = {
            onClick { onLogout() }
        }) {
            Text("LogOut")
        }
    }
}

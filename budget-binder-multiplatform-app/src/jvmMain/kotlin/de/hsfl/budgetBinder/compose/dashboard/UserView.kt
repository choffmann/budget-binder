package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.presentation.UiState

@Composable
fun UserView(
    state: State<Any>,
    onUpdate: () -> Unit,
    onLogout: () -> Unit
) {
    val viewState by remember { state }
    Box(modifier = Modifier.fillMaxSize())
    Column {
        when (viewState) {
            is UiState.Success<*> -> {
                Text((viewState as UiState.Success<*>).element.toString())
            }
            is UiState.Error -> {
                Text((viewState as UiState.Error).error)
            }
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
        }
        Row {
            Button(onClick = { onUpdate() }) {
                Text("Update")
            }
            Button(onClick = { onLogout() }) {
                Text("Logout")
            }
        }

    }
}

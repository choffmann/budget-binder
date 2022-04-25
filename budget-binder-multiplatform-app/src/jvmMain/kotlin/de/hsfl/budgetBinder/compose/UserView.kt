package de.hsfl.budgetBinder.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import de.hsfl.budgetBinder.ApplicationFlow
import de.hsfl.budgetBinder.UIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.client.Client

@Composable
fun UserView() {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val applicationFlow = ApplicationFlow(Client(), scope)
    val uiState by applicationFlow.uiState.collectAsState(scope)
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize())
        Column {
            when (uiState) {
                is UIState.Path -> {
                    Text((uiState as UIState.Path).data)
                }
                is UIState.Error -> {
                    Text((uiState as UIState.Error).error.toString())
                }
                is UIState.User -> {
                    Text((uiState as UIState.User).user.toString())
                }
                else -> {
                    Text("Something is not correct!")
                }
            }
            Button(onClick = { applicationFlow.update() }) {
                Text("Update")
            }
        }
    }
}
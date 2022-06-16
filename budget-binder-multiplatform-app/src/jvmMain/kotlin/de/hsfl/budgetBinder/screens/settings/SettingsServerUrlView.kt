package de.hsfl.budgetBinder.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.viewmodel.settings.EditServerUrlEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEditServerUrlViewModel
import org.kodein.di.instance

@Composable
fun SettingsServerUrlView(modifier: Modifier = Modifier) {
    val viewModel: SettingsEditServerUrlViewModel by di.instance()
    val serverUrl = viewModel.serverUrl.collectAsState()

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = serverUrl.value,
            onValueChange = { },
            label = { Text("Server Url") },
            enabled = false
        )
        Text(
            text = "You can't change the Server URL when you are logged in",
            style = MaterialTheme.typography.caption
        )
        Button(onClick = { viewModel.onEvent(EditServerUrlEvent.GoBack) }) {
            Text("Back")
        }
    }
}

package de.hsfl.budgetBinder.prototype.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.StateManager

@Composable
fun SettingsComponent() {
    SettingsView()
}

@Composable
private fun SettingsView() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            UserView()
            SettingsList()
        }
    }
}

@Composable
private fun UserView() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarImage(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally).size(128.dp))
        Text(
            text = "Cedrik Hoffmann",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(text = "hoffmann@mail.com", style = MaterialTheme.typography.subtitle1, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SettingsList() {
    Column {
        ListItem(text = { Text("Dark Mode") }, icon = { Icon(Icons.Filled.Build, contentDescription = null) }, trailing = {
            Switch(modifier = Modifier.padding(start = 8.dp),
                checked = StateManager.darkMode.value,
                onCheckedChange = { StateManager.darkMode.value = it })
        })
    }
}

@Composable
expect fun AvatarImage(modifier: Modifier = Modifier)
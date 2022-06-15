package de.hsfl.budgetBinder.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsViewModel
import org.kodein.di.instance

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsMenuView(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val viewModel: SettingsViewModel by di.instance()
    val dataFlow: DataFlow by di.instance()
    val darkMode = dataFlow.darkModeState.collectAsState(scope.coroutineContext)

    Column(modifier = modifier) {
        Divider()
        ListItem(text = { Text("Dark Mode") },
            modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnToggleDarkMode) }),
            icon = { Icon(Icons.Filled.Build, contentDescription = null) },
            trailing = {
                Switch(modifier = Modifier.padding(start = 8.dp),
                    checked = darkMode.value,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.OnToggleDarkMode) })
            })
        Divider()
        ListItem(modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnChangeToSettingsUserEdit) }),
            text = { Text("Account") },
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
            trailing = { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null) })
        Divider()
        ListItem(modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnChangeToSettingsServerUrlEdit) }),
            text = { Text("Server") },
            icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
            trailing = { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null) })
    }
}

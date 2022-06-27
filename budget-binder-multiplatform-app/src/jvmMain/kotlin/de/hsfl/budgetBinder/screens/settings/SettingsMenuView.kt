package de.hsfl.budgetBinder.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.compose.dialog.DeleteUserDialog
import de.hsfl.budgetBinder.compose.icon.*
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsViewModel
import org.kodein.di.instance

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsMenuView(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val viewModel: SettingsViewModel by di.instance()
    val darkMode = viewModel.darkModeState.collectAsState(scope.coroutineContext)
    val dialogState = viewModel.dialogState.collectAsState()

    Column(modifier = modifier) {
        Divider()
        ListItem(text = { Text("Dark Mode") },
            modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnToggleDarkMode) }),
            icon = { DarkModeIcon() },
            trailing = {
                Switch(modifier = Modifier.padding(start = 8.dp),
                    checked = darkMode.value,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.OnToggleDarkMode) })
            })
        Divider()
        ListItem(modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnChangeToSettingsUserEdit) }),
            text = { Text("Account") },
            icon = { AccountIcon() },
            trailing = { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null) })
        Divider()
        ListItem(modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnChangeToSettingsServerUrlEdit) }),
            text = { Text("Server") },
            icon = { ServerIcon() },
            trailing = { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null) })
        Divider()
        ListItem(modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnLogoutAllDevices) }),
            text = { Text("Logout on all device") },
            icon = { LogoutIcon() })
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "Danger Zone",
            color = Color.Red,
            style = MaterialTheme.typography.subtitle2
        )
        Divider()
        ListItem(modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnResetSettings) }),
            text = { Text("Reset the App") },
            icon = { ResetIcon() })
        Divider()
        ListItem(modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnResetSettings) }),
            text = { Text("Reset the App") },
            icon = { ResetIcon() })
        ListItem(modifier = Modifier.clickable(onClick = { viewModel.onEvent(SettingsEvent.OnDeleteUser) }),
            text = { Text("Delete my User") },
            icon = { DeleteForeverIcon() })
    }
    DeleteUserDialog(
        openDialog = dialogState.value,
        onDismiss = { viewModel.onEvent(SettingsEvent.OnDeleteDialogDismiss) },
        onConfirm = { viewModel.onEvent(SettingsEvent.OnDeleteDialogConfirm) }
    )
}

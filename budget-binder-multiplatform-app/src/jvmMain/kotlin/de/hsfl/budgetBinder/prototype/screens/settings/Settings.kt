package de.hsfl.budgetBinder.prototype.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.StateManager.darkMode
import de.hsfl.budgetBinder.prototype.StateManager.screenState
import de.hsfl.budgetBinder.prototype.StateManager.userState
import de.hsfl.budgetBinder.prototype.User

val settingsScreenState = mutableStateOf<SettingsScreens>(SettingsScreens.Menu)

@Composable
fun SettingsComponent() {
    SettingsView()
}

@Composable
private fun SettingsView() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            UserView()
            when (settingsScreenState.value) {
                is SettingsScreens.Menu -> {
                    MenuView(modifier = Modifier.padding(top = 16.dp), onListClick = {
                        when (it) {
                            "Account" -> settingsScreenState.value = SettingsScreens.Account
                            "Server" -> settingsScreenState.value = SettingsScreens.Server
                        }
                    })
                }
                is SettingsScreens.Account -> {
                    UserSettings()
                }
                is SettingsScreens.Server -> {
                    Text("Server Settings")
                }
            }
        }
    }
}

@Composable
private fun UserSettings() {
    val firstNameState = remember { mutableStateOf(userState.value.firstName) }
    val lastNameState = remember { mutableStateOf(userState.value.lastName) }
    val emailState = remember { mutableStateOf(userState.value.email) }
    val passwordState = remember { mutableStateOf(userState.value.password) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            value = firstNameState.value,
            onValueChange = { firstNameState.value = it },
            label = { Text("Firstname") },
            singleLine = true
        )
        TextField(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            value = lastNameState.value,
            onValueChange = { lastNameState.value = it },
            label = { Text("Lastname") },
            singleLine = true
        )
        TextField(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            value = emailState.value,
            enabled = false,
            onValueChange = { emailState.value = emailState.value },
            label = { Text("Email") },
            singleLine = true,
        )
        TextField(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Box(modifier = Modifier.fillMaxSize().align(Alignment.CenterHorizontally)) {
            Row {
                OutlinedButton(modifier = Modifier.weight(1F).padding(16.dp),
                    onClick = { settingsScreenState.value = SettingsScreens.Menu }) {
                    Text("Back")
                }
                Button(modifier = Modifier.weight(1F).padding(16.dp), onClick = {
                    userState.value = User(
                        firstName = firstNameState.value,
                        lastName = lastNameState.value,
                        email = emailState.value,
                        password = passwordState.value
                    )
                }) {
                    Text("Update")
                }
            }
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
            text = "${userState.value.firstName} ${userState.value.lastName}",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(text = userState.value.email, style = MaterialTheme.typography.subtitle1, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MenuView(
    modifier: Modifier = Modifier, onListClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        Divider()
        ListItem(text = { Text("Dark Mode") },
            icon = { Icon(Icons.Filled.Build, contentDescription = null) },
            trailing = {
                Switch(modifier = Modifier.padding(start = 8.dp),
                    checked = darkMode.value,
                    onCheckedChange = { darkMode.value = it })
            })
        Divider()
        ListItem(modifier = Modifier.clickable(onClick = { onListClick("Account") }),
            text = { Text("Account") },
            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
            trailing = { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null) })
        Divider()
        ListItem(modifier = Modifier.clickable(onClick = { onListClick("Server") }),
            text = { Text("Server") },
            icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
            trailing = { Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null) })
    }
}

@Composable
expect fun AvatarImage(modifier: Modifier = Modifier)


sealed class SettingsScreens {
    object Menu : SettingsScreens()
    object Account : SettingsScreens()
    object Server : SettingsScreens()
}
package de.hsfl.budgetBinder.compose.screens.settings

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.compose.StateManager
import de.hsfl.budgetBinder.compose.StateManager.darkMode
import de.hsfl.budgetBinder.compose.StateManager.serverState
import de.hsfl.budgetBinder.compose.StateManager.userState
import de.hsfl.budgetBinder.compose.di
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch
import org.kodein.di.instance

private val settingsScreenState = mutableStateOf<SettingsScreens>(SettingsScreens.Menu)

@Composable
internal fun SettingsComponent() {
    val scope = rememberCoroutineScope()
    val viewModel: SettingsViewModel by di.instance()
    val viewState = viewModel.state.collectAsState(scope)

    SettingsView(
        viewModelState = viewState,
        onUserChangeClicked = { userToChange ->
            viewModel.changeMyUser(userToChange)
        },
        onUserChangeSuccess = { newUser ->
            userState.value = newUser
            settingsScreenState.value = SettingsScreens.Menu
        }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SettingsView(
    viewModelState: State<Any>,
    onUserChangeClicked: (User.In) -> Unit,
    onUserChangeSuccess: (User) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            UserView()
            AnimatedContent(targetState = settingsScreenState.value, transitionSpec = {
                if (targetState.weight > initialState.weight) {
                    slideInHorizontally { fullWidth -> fullWidth } + fadeIn() with slideOutHorizontally { fullWidth -> -fullWidth }
                } else {
                    slideInHorizontally { fullWidth -> -fullWidth } + fadeIn() with slideOutHorizontally { fullWidth -> fullWidth }
                }.using(SizeTransform(clip = false))
            }) { state ->
                when (state) {
                    is SettingsScreens.Menu -> {
                        MenuView(modifier = Modifier.padding(top = 16.dp), onListClick = {
                            when (it) {
                                "Account" -> settingsScreenState.value = SettingsScreens.Account
                                "Server" -> settingsScreenState.value = SettingsScreens.Server
                            }
                        })
                    }
                    is SettingsScreens.Account -> {
                        UserSettings(state = viewModelState,onUserChangeClicked = onUserChangeClicked, onSuccess = onUserChangeSuccess)
                    }
                    is SettingsScreens.Server -> {
                        ServerSettings()
                    }
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
            text = "${userState.value.firstName} ${userState.value.name}",
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
    modifier: Modifier = Modifier,
    onListClick: (String) -> Unit
) {
    Column(modifier = modifier) {
        Divider()
        ListItem(text = { Text("Dark Mode") },
            modifier = Modifier.clickable(onClick = { darkMode.value = !darkMode.value }),
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
private fun UserSettings(
    state: State<Any>,
    onUserChangeClicked: (User.In) -> Unit,
    onSuccess: (User) -> Unit
) {
    val firstNameState = remember { mutableStateOf(userState.value.firstName) }
    val lastNameState = remember { mutableStateOf(userState.value.name) }
    val emailState = remember { mutableStateOf(userState.value.email) }
    // TODO: Fill with real password length ?
    val passwordState = remember { mutableStateOf("........") }
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
                    onUserChangeClicked(User.In(
                        firstName = firstNameState.value,
                        name = lastNameState.value,
                        email = emailState.value,
                        password = passwordState.value
                    ))
                }) {
                    Text("Update")
                }
            }
        }
    }
    HandleViewState(viewState = state, onSuccess = onSuccess)
}

@Composable
private fun ServerSettings() {
    // TODO: Server URL is wrong
    val serverUrlState = remember { mutableStateOf(serverState.value) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp).fillMaxWidth(),
            value = serverUrlState.value,
            enabled = false,
            onValueChange = { serverUrlState.value = it },
            label = { Text("Server URL") },
            singleLine = true
        )
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "You can't change the Server URL when you are logged in",
            style = MaterialTheme.typography.caption
        )
        Button(onClick = { settingsScreenState.value = SettingsScreens.Menu }) {
            Text("Back")
        }
    }
}

@Composable
private fun HandleViewState(viewState: State<Any>, onSuccess: (User) -> Unit ) {
    val state = remember { viewState }
    val scope = rememberCoroutineScope()
    when(state.value) {
        is UiState.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        is UiState.Success<*> -> {
            onSuccess((state.value as UiState.Success<*>).element as User)
        }
        is UiState.Error -> {
            scope.launch {
                StateManager.scaffoldState.snackbarHostState.showSnackbar(
                    message = "Error: ${(state.value as UiState.Error).error}",
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Long
                )
            }
        }
        is UiState.Empty -> {}
    }
}

@Composable
expect fun AvatarImage(modifier: Modifier = Modifier)


// weight 0 => Root
// weight 1 => Child from 0
sealed class SettingsScreens(val weight: Int) {
    object Menu : SettingsScreens(0)
    object Account : SettingsScreens(1)
    object Server : SettingsScreens(1)
}
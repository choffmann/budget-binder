package de.hsfl.budgetBinder.prototype.screens.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.PrototypeScreen
import de.hsfl.budgetBinder.prototype.Server
import de.hsfl.budgetBinder.prototype.StateManager.isLoggedIn
import de.hsfl.budgetBinder.prototype.StateManager.scaffoldState
import de.hsfl.budgetBinder.prototype.StateManager.screenState
import de.hsfl.budgetBinder.prototype.StateManager.serverState
import de.hsfl.budgetBinder.prototype.StateManager.userState
import de.hsfl.budgetBinder.prototype.screens.TextFieldState
import de.hsfl.budgetBinder.prototype.screens.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val loginState = mutableStateOf<UiState>(UiState.Nothing)
private val emailTextFieldState = mutableStateOf<TextFieldState>(TextFieldState.Nothing)

@Composable
fun LoginComponent() {
    LoginView()
}

@Composable
private fun LoginView() {
    val openDialog = remember { mutableStateOf(false) }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Header(text = "Welcome back!")
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LoginTextField(onButtonClicked = { email, password ->
            emailState.value = email
            passwordState.value = password
            openDialog.value = true
        })
    }
    Box(modifier = Modifier.fillMaxSize()) {
        TextButton(modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { screenState.value = PrototypeScreen.Register }) {
            Text("Or Register your Account here")
        }
    }
    when (loginState.value) {
        is UiState.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        is UiState.Success -> {
            screenState.value = PrototypeScreen.Home
        }
        is UiState.Error -> {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Error: ${(loginState.value as UiState.Error).msg}",
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Long
                )
            }
        }
        is UiState.Nothing -> {}
    }
    ServerUrlDialog(openDialog = openDialog, onConfirm = {
        serverState.value = Server(serverUrl = it)
        scope.launch {
            // Fake loading
            loginState.value = UiState.Loading
            delay(2000L)
            if (userState.value.email == emailState.value && userState.value.password == passwordState.value) {
                isLoggedIn.value = true
                loginState.value = UiState.Success
            } else {
                // Error message, wich should come from Backend
                loginState.value = UiState.Error(msg = "Wrong Email or Password")
            }
        }
    })
}

@Composable
fun Header(text: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppIcon(
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp).size(128.dp)
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = text,
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LoginTextField(modifier: Modifier = Modifier, onButtonClicked: (String, String) -> Unit) {
    val emailTextState = remember { mutableStateOf("") }
    val passwordTextState = remember { mutableStateOf("") }
    val emailRegex =
        "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".toRegex()
    Column(modifier = modifier) {
        OutlinedTextField(
            value = emailTextState.value,
            onValueChange = {
                emailTextState.value = it
                emailTextFieldState.value = TextFieldState.Nothing
            },
            label = { Text("Email") },
            singleLine = true,
            isError = emailTextFieldState.value is TextFieldState.Error
        )
        if (emailTextFieldState.value is TextFieldState.Error) Text(
            text = (emailTextFieldState.value as TextFieldState.Error).msg,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.error
        )
        OutlinedTextField(
            value = passwordTextState.value,
            onValueChange = { passwordTextState.value = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp), onClick = {
            if (emailRegex.matches(emailTextState.value)) {
                onButtonClicked(emailTextState.value, passwordTextState.value)
            } else {
                emailTextFieldState.value = TextFieldState.Error("Please enter an actual email address")
            }
        }) {
            Text("Login")
        }
    }

}

@Composable
expect fun ServerUrlDialog(openDialog: MutableState<Boolean>, onConfirm: (String) -> Unit)

@Composable
expect fun AppIcon(modifier: Modifier = Modifier)

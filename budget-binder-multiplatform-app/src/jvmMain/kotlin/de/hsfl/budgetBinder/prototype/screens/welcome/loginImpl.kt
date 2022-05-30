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
import de.hsfl.budgetBinder.prototype.StateManager.scaffoldState
import de.hsfl.budgetBinder.prototype.StateManager.screenState
import de.hsfl.budgetBinder.prototype.StateManager.serverState
import de.hsfl.budgetBinder.prototype.StateManager.userState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val loginState = mutableStateOf<LoginState>(LoginState.Nothing)

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
    Header()
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
        is LoginState.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        is LoginState.Success -> {
            screenState.value = PrototypeScreen.Home
        }
        is LoginState.Error -> {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Error: ${(loginState.value as LoginState.Error).msg}",
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Long
                )
            }
        }
        is LoginState.Nothing -> {}
    }
    ServerUrlDialog(openDialog = openDialog, onConfirm = {
        serverState.value = Server(serverUrl = it)
        scope.launch {
            // Fake loading
            loginState.value = LoginState.Loading
            delay(2000L)
            if (userState.value.email == emailState.value && userState.value.password == passwordState.value) {
                loginState.value = LoginState.Success
            } else {
                // Error message, wich should come from Backend
                loginState.value = LoginState.Error(msg = "Wrong Email or Password")
            }
        }
    })
}

@Composable
private fun Header() {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppIcon(
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth()
                .padding(top = 32.dp, start = 16.dp, end = 16.dp).size(128.dp)
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Welcome back!",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LoginTextField(modifier: Modifier = Modifier, onButtonClicked: (String, String) -> Unit) {
    val emailTextState = remember { mutableStateOf("") }
    val passwordTextState = remember { mutableStateOf("") }
    Column(modifier = modifier) {
        OutlinedTextField(
            value = emailTextState.value,
            onValueChange = { emailTextState.value = it },
            label = { Text("Email") },
            singleLine = true
        )
        OutlinedTextField(
            value = passwordTextState.value,
            onValueChange = { passwordTextState.value = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
            onClick = { onButtonClicked(emailTextState.value, passwordTextState.value) }) {
            Text("Login")
        }
    }

}

@Composable
expect fun ServerUrlDialog(openDialog: MutableState<Boolean>, onConfirm: (String) -> Unit)

@Composable
expect fun AppIcon(modifier: Modifier = Modifier)

private sealed class LoginState {
    object Nothing : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val msg: String) : LoginState()
}
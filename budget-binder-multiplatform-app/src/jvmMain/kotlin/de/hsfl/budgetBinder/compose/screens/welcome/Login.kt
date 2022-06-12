package de.hsfl.budgetBinder.compose.screens.welcome

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
import de.hsfl.budgetBinder.common.Constants.BASE_URL
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.compose.StateManager.isLoggedIn
import de.hsfl.budgetBinder.compose.StateManager.scaffoldState
import de.hsfl.budgetBinder.compose.StateManager.screenState
import de.hsfl.budgetBinder.compose.StateManager.userState
import de.hsfl.budgetBinder.compose.di
import de.hsfl.budgetBinder.compose.screens.TextFieldState
import de.hsfl.budgetBinder.compose.utils.validateEmail
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.viewmodel.LoginViewModel
import de.hsfl.budgetBinder.presentation.Screen
import kotlinx.coroutines.*
import org.kodein.di.instance

private val emailTextFieldState = mutableStateOf<TextFieldState>(TextFieldState.Nothing)
private val openDialog = mutableStateOf(false)

@Composable
internal fun LoginComponent() {
    val scope = rememberCoroutineScope()
    val viewModel: LoginViewModel by di.instance()
    val viewState = viewModel.state.collectAsState(scope)

    LoginView(scope = scope, viewModelState = viewState, onServerDialogConfirm = { url, email, password ->
        // URL REGEX CHECK ?
        BASE_URL = url
        viewModel.login(email, password)
    }, onLoginSuccess = { user ->
        userState.value = user
        isLoggedIn.value = true
        screenState.value = Screen.Dashboard
    })
}

@Composable
private fun LoginView(
    scope: CoroutineScope,
    viewModelState: State<Any>,
    onServerDialogConfirm: (url: String, email: String, password: String) -> Unit,
    onLoginSuccess: (User) -> Unit
) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val loginState = remember { viewModelState }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Header(text = "Welcome back!")
        LoginTextField(onButtonClicked = { email, password ->
            emailState.value = email
            passwordState.value = password
            openDialog.value = true
        })
        Box(modifier = Modifier.fillMaxSize()) {
            TextButton(modifier = Modifier.align(Alignment.BottomCenter),
                onClick = { screenState.value = Screen.Register }) {
                Text("Or Register your Account here")
            }
        }
    }
    when (loginState.value) {
        is UiState.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        is UiState.Success<*> -> {
            onLoginSuccess((loginState.value as UiState.Success<*>).element as User)
        }
        is UiState.Error -> {
            scope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Error: ${(loginState.value as UiState.Error).error}",
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Long
                )
            }
        }
        is UiState.Empty -> {}
    }
    ServerUrlDialog(openDialog = openDialog, onConfirm = {
        onServerDialogConfirm(it, emailState.value, passwordState.value)
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
    Column(modifier = modifier) {
        OutlinedTextField(
            value = emailTextState.value,
            onValueChange = {
                emailTextState.value = it
                emailTextFieldState.value = TextFieldState.Nothing
            },
            label = { Text("Email") },
            singleLine = true,
            isError = emailTextFieldState.value is TextFieldState.Error,
            enabled = !openDialog.value
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !openDialog.value
        )
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
            enabled = !openDialog.value,
            onClick = {
                if (validateEmail(emailTextState.value)) {
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

package de.hsfl.budgetBinder.compose.screens.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Constants.BASE_URL
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.compose.StateManager
import de.hsfl.budgetBinder.compose.StateManager.isLoggedIn
import de.hsfl.budgetBinder.compose.StateManager.screenState
import de.hsfl.budgetBinder.compose.StateManager.userState
import de.hsfl.budgetBinder.compose.di
import de.hsfl.budgetBinder.compose.screens.TextFieldState
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.compose.utils.validateEmail
import de.hsfl.budgetBinder.presentation.viewmodel.RegisterViewModel
import kotlinx.coroutines.*
import org.kodein.di.instance

private val emailTextFieldState = mutableStateOf<TextFieldState>(TextFieldState.Nothing)

@Composable
internal fun RegisterComponent() {
    val scope = rememberCoroutineScope()
    val viewModel: RegisterViewModel by di.instance()
    val viewState = viewModel.state.collectAsState(scope)

    RegisterView(scope = scope, viewModelState = viewState,
        onServerDialogConfirm = { url, user ->
            BASE_URL = url
            viewModel.register(user)
        },
        onSuccess = { user ->
            userState.value = user
            isLoggedIn.value = true
            screenState.value = Screen.Home
        })
}

@Composable
private fun RegisterView(
    scope: CoroutineScope,
    viewModelState: State<Any>,
    onServerDialogConfirm: (url: String, user: User.In) -> Unit,
    onSuccess: (User) -> Unit
) {
    val registerState = remember { viewModelState }
    val registerUserState = remember { mutableStateOf(User.In("", "", "", "")) }
    val openDialog = remember { mutableStateOf(false) }
    Column {
        Header(text = "Welcome to Budget Binder!")
        RegisterTextField(onButtonClicked = {
            registerUserState.value = it
            openDialog.value = true
        })
    }
    Box(modifier = Modifier.fillMaxSize()) {
        TextButton(modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { screenState.value = Screen.Login }) {
            Text("Or Login with your Account")
        }
    }
    when (registerState.value) {
        is UiState.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        is UiState.Success<*> -> {
            onSuccess((registerState.value as UiState.Success<*>).element as User)
        }
        is UiState.Error -> {
            scope.launch {
                StateManager.scaffoldState.snackbarHostState.showSnackbar(
                    message = "Error: ${(registerState.value as UiState.Error).error}",
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Long
                )
            }
        }
        is UiState.Empty -> {}
    }
    ServerUrlDialog(openDialog = openDialog, onConfirm = {
        onServerDialogConfirm(it, registerUserState.value)
    })
}

@Composable
private fun RegisterTextField(onButtonClicked: (User.In) -> Unit) {
    val firstNameTextState = remember { mutableStateOf("") }
    val lastNameTextState = remember { mutableStateOf("") }
    val emailTextState = remember { mutableStateOf("") }
    val passwordTextState = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = firstNameTextState.value,
            onValueChange = { firstNameTextState.value = it },
            label = { Text("Firstname") },
            singleLine = true
        )
        OutlinedTextField(
            value = lastNameTextState.value,
            onValueChange = { lastNameTextState.value = it },
            label = { Text("Lastname") },
            singleLine = true
        )
        OutlinedTextField(value = emailTextState.value, onValueChange = {
            emailTextState.value = it
            emailTextFieldState.value = TextFieldState.Nothing
        }, label = { Text("Email") }, singleLine = true, isError = emailTextFieldState.value is TextFieldState.Error
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
        Button(modifier = Modifier.padding(16.dp), onClick = {
            if (validateEmail(emailTextState.value)) {
                onButtonClicked(
                    User.In(
                        firstName = firstNameTextState.value,
                        name = lastNameTextState.value,
                        email = emailTextState.value,
                        password = passwordTextState.value
                    )
                )
            } else {
                emailTextFieldState.value = TextFieldState.Error("Please enter an actual email address")
            }
        }) {
            Text("Register")
        }
    }
}
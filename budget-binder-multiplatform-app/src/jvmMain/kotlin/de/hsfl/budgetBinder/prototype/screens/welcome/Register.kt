package de.hsfl.budgetBinder.prototype.screens.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.PrototypeScreen
import de.hsfl.budgetBinder.prototype.StateManager
import de.hsfl.budgetBinder.prototype.StateManager.isLoggedIn
import de.hsfl.budgetBinder.prototype.StateManager.userState
import de.hsfl.budgetBinder.prototype.User
import de.hsfl.budgetBinder.prototype.screens.TextFieldState
import de.hsfl.budgetBinder.prototype.screens.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val registerState = mutableStateOf<UiState>(UiState.Nothing)
private val emailTextFieldState = mutableStateOf<TextFieldState>(TextFieldState.Nothing)

@Composable
fun RegisterComponent() {
    RegisterView()
}

@Composable
private fun RegisterView() {
    val scope = rememberCoroutineScope()
    Column {
        Header(text = "Hello!")
        RegisterTextField(onRegister = {
            scope.launch {
                registerState.value = UiState.Loading
                delay(2000L)
                if (it.firstName.isNotBlank() && it.lastName.isNotBlank() && it.email.isNotBlank() && it.password.isNotBlank() && emailTextFieldState.value is TextFieldState.Nothing) {
                    userState.value = it
                    registerState.value = UiState.Success
                } else {
                    registerState.value = UiState.Error(msg = "Please Enter all TextFields")
                }
            }
        })
    }
    Box(modifier = Modifier.fillMaxSize()) {
        TextButton(modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { StateManager.screenState.value = PrototypeScreen.Login }) {
            Text("Or Login with your Account")
        }
    }
    when (registerState.value) {
        is UiState.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        is UiState.Success -> {
            isLoggedIn.value = true
            StateManager.screenState.value = PrototypeScreen.Home
        }
        is UiState.Error -> {
            scope.launch {
                StateManager.scaffoldState.snackbarHostState.showSnackbar(
                    message = "Error: ${(registerState.value as UiState.Error).msg}",
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Long
                )
            }
        }
        is UiState.Nothing -> {}
    }
}

@Composable
private fun RegisterTextField(onRegister: (User) -> Unit) {
    val firstNameTextState = remember { mutableStateOf("") }
    val lastNameTextState = remember { mutableStateOf("") }
    val emailTextState = remember { mutableStateOf("") }
    val passwordTextState = remember { mutableStateOf("") }
    val emailRegex =
        "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".toRegex()
    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = firstNameTextState.value,
            onValueChange = { firstNameTextState.value = it },
            label = { Text("Firstname") },
            singleLine = true
        )
        OutlinedTextField(value = lastNameTextState.value,
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
        OutlinedTextField(value = passwordTextState.value,
            onValueChange = { passwordTextState.value = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Button(modifier = Modifier.padding(16.dp), onClick = {
            if (emailRegex.matches(emailTextState.value)) {
                onRegister(
                    User(
                        firstName = firstNameTextState.value,
                        lastName = lastNameTextState.value,
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
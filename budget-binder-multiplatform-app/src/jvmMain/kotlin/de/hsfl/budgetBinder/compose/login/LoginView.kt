package de.hsfl.budgetBinder.compose.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import de.hsfl.budgetBinder.presentation.UiState

@Composable
fun LoginView(
    state: State<Any>,
    onLoginButtonPressed: (email: String, password: String) -> Unit,
    onLoginSuccess: () -> Unit
) {
    var emailTextFieldState by remember { mutableStateOf("") }
    var passwordTextFieldState by remember { mutableStateOf("") }
    val viewState by remember { state }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val localFocusManager = LocalFocusManager.current

        OutlinedTextField(
            value = emailTextFieldState,
            onValueChange = { emailTextFieldState = it },
            label = { Text("Email") },
        )

        OutlinedTextField(
            value = passwordTextFieldState,
            onValueChange = { passwordTextFieldState = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )


        when (viewState) {
            is UiState.Success<*> -> {
                onLoginSuccess()
            }
            is UiState.Error -> {
                Text(
                    text = (viewState as UiState.Error).error,
                    color = MaterialTheme.colors.error,
                    fontWeight = FontWeight.Bold
                )
            }
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            else -> {}
        }

        Button(onClick = {
            localFocusManager.clearFocus()
            onLoginButtonPressed(emailTextFieldState, passwordTextFieldState)
        }) {
            Text("Login")
        }
    }
}
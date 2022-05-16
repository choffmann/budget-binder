package de.hsfl.budgetBinder.compose

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
import de.hsfl.budgetBinder.presentation.LoginState

@Composable
fun LoginView(
    onLoginButtonPressed: (email: String, password: String) -> Unit,
    state: State<Any>
) {
    var emailTextFieldState by remember { mutableStateOf("") }
    var passwordTextFieldState by remember { mutableStateOf("") }
    val viewState by remember { state }

    MaterialTheme {
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

            /*when (viewState) {
                is LoginState.Success -> {
                    Text((viewState as LoginState.Success).login.toString())
                }
                is LoginState.Error -> {
                    Text(
                        text = (viewState as LoginState.Error).error,
                        color = MaterialTheme.colors.error,
                        fontWeight = FontWeight.Bold
                    )
                }
                is LoginState.Loading -> {
                    CircularProgressIndicator()
                }
            }*/

            Button(onClick = {
                localFocusManager.clearFocus()
                onLoginButtonPressed(emailTextFieldState, passwordTextFieldState)
                //viewModel.auth(emailTextFieldState, passwordTextFieldState)

            }) {
                Text("Login")
            }
        }
    }
}
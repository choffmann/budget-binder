package de.hsfl.budgetBinder.compose.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import de.hsfl.budgetBinder.presentation.UiState

@Composable
fun RegisterView(
    state: State<Any>,
    onRegisterButtonPressed: (firstName: String, lastName: String, email: String, password: String) -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var firstNameTextState by remember { mutableStateOf("") }
    var lastNameTextState by remember { mutableStateOf("") }
    var emailTextState by remember { mutableStateOf("") }
    var passwordTextState by remember { mutableStateOf("") }
    val viewState by remember { state }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = firstNameTextState,
            onValueChange = { firstNameTextState = it },
            label = { Text("Fistname") },
        )

        OutlinedTextField(
            value = lastNameTextState,
            onValueChange = { lastNameTextState = it },
            label = { Text("Lastname") },
        )

        OutlinedTextField(
            value = emailTextState,
            onValueChange = { emailTextState = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = passwordTextState,
            onValueChange = { passwordTextState = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Button(onClick = {
            onRegisterButtonPressed(
                firstNameTextState,
                lastNameTextState,
                emailTextState,
                passwordTextState
            )
        }) {
            Text("Register")
        }

        when (viewState) {
            is UiState.Success<*> -> {
                onRegisterSuccess()
            }
            is UiState.Error -> {
                Text((viewState as UiState.Error).error)
            }
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
        }
    }
}
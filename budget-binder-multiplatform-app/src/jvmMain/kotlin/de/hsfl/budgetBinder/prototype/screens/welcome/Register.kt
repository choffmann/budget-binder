package de.hsfl.budgetBinder.prototype.screens.welcome

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.PrototypeScreen
import de.hsfl.budgetBinder.prototype.StateManager

@Composable
fun RegisterComponent() {
    RegisterView()
}

@Composable
private fun RegisterView() {
    Column {
        Header(text = "Hello!")
        RegisterTextField()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        TextButton(modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { StateManager.screenState.value = PrototypeScreen.Login }) {
            Text("Or Login with your Account")
        }
    }
}

@Composable
private fun RegisterTextField() {
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
        Button(modifier = Modifier.padding(16.dp), onClick = {}) {
            Text("Register")
        }
    }
}
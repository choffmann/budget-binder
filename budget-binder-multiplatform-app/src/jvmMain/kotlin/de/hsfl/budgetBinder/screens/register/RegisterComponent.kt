package de.hsfl.budgetBinder.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.compose.icon.AppIcon
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.compose.textfield.EmailTextField
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.auth.register.RegisterEvent
import de.hsfl.budgetBinder.presentation.viewmodel.auth.register.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun RegisterComponent() {
    val viewModel: RegisterViewModel by di.instance()
    val firstNameTextState = viewModel.firstNameText.collectAsState()
    val lastNameTextState = viewModel.lastNameText.collectAsState()
    val emailTextState = viewModel.emailText.collectAsState()
    val passwordTextState = viewModel.passwordText.collectAsState()
    val confirmedPasswordTextState = viewModel.confirmedPasswordText.collectAsState()
    val loadingState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(RegisterEvent.LifeCycle(LifecycleEvent.OnLaunch))
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> {
                    // TODO: Refactor this, it's working but ahh
                    loadingState.value = true
                }
                else -> loadingState.value = false
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(RegisterEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    if (loadingState.value) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppIcon(modifier = Modifier.size(128.dp).padding(8.dp))
        Text(text = "Hello there 👋", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = firstNameTextState.value.firstName,
            onValueChange = { viewModel.onEvent(RegisterEvent.EnteredFirstname(it)) },
            label = { Text("Firstname") },
            singleLine = true
        )
        OutlinedTextField(
            value = lastNameTextState.value.lastName,
            onValueChange = { viewModel.onEvent(RegisterEvent.EnteredLastname(it)) },
            label = { Text("Lastname") },
            singleLine = true
        )
        EmailTextField(
            value = emailTextState.value.email,
            onValueChange = { viewModel.onEvent(RegisterEvent.EnteredEmail(it)) },
            label = { Text("Email") },
            isError = !emailTextState.value.emailValid,
            enabled = !loadingState.value
        )
        OutlinedTextField(
            value = passwordTextState.value.password,
            onValueChange = { viewModel.onEvent(RegisterEvent.EnteredPassword(it)) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        OutlinedTextField(
            value = confirmedPasswordTextState.value.confirmedPassword,
            onValueChange = { viewModel.onEvent(RegisterEvent.EnteredConfirmedPassword(it))},
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = !confirmedPasswordTextState.value.confirmedPasswordValid,
            enabled = !loadingState.value,
            trailingIcon = {
                if (!confirmedPasswordTextState.value.confirmedPasswordValid) {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
            }
        )
        if(!confirmedPasswordTextState.value.confirmedPasswordValid) {
            Text(text = "Password didn't match", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.error)
        }


        Button(onClick = { viewModel.onEvent(RegisterEvent.OnRegister) }) {
            Text("Register")
        }
        Box(modifier = Modifier.fillMaxSize()) {
            TextButton(modifier = Modifier.align(Alignment.BottomCenter), onClick = {
                viewModel.onEvent(RegisterEvent.OnLoginScreen)
            }) {
                Text("Or Login with your Account")
            }
        }
    }
}

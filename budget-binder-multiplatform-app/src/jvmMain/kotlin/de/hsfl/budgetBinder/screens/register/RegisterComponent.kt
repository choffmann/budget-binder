package de.hsfl.budgetBinder.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import de.hsfl.budgetBinder.compose.di
import de.hsfl.budgetBinder.compose.textfield.EmailTextField
import de.hsfl.budgetBinder.presentation.login.LoginViewModel
import de.hsfl.budgetBinder.presentation.register.RegisterEvent
import de.hsfl.budgetBinder.presentation.register.RegisterViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun RegisterComponent() {
    val scope = rememberCoroutineScope()
    val viewModel: RegisterViewModel by di.instance()
    val firstNameTextState = viewModel.firstNameText.collectAsState(scope.coroutineContext)
    val lastNameTextState = viewModel.lastNameText.collectAsState(scope.coroutineContext)
    val emailTextState = viewModel.emailText.collectAsState(scope.coroutineContext)
    val passwordTextState = viewModel.passwordText.collectAsState(scope.coroutineContext)
    val scaffoldState = rememberScaffoldState()
    val loadingState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RegisterViewModel.UiEvent.ShowLoading -> {
                    // TODO: Refactor this, it's working but ahh
                    loadingState.value = true
                }
                is RegisterViewModel.UiEvent.ShowError -> {
                    loadingState.value = false
                    scaffoldState.snackbarHostState.showSnackbar(message = event.msg, actionLabel = "Dissmiss")
                }
            }
        }
    }

    Scaffold(scaffoldState = scaffoldState) {
        if (loadingState.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = firstNameTextState.value.firstName,
                onValueChange = { viewModel.onEvent(RegisterEvent.EnteredFirstname(it)) },
                label = { Text("Fistname") },
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
                isError = !emailTextState.value.emailValide,
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
            Button(onClick = { viewModel.onEvent(RegisterEvent.OnRegister) }) {
                Text("Register")
            }
            Button(onClick = { viewModel.onEvent(RegisterEvent.OnChangeToLogin) }) {
                Text("Change to Login")
            }
        }
    }
}
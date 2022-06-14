package de.hsfl.budgetBinder.screens.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.compose.icon.AppIcon
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.compose.textfield.EmailTextField
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.register.RegisterEvent
import de.hsfl.budgetBinder.presentation.viewmodel.register.RegisterViewModel
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
                is UiEvent.ShowLoading -> {
                    // TODO: Refactor this, it's working but ahh
                    loadingState.value = true
                }
                is UiEvent.ShowError -> {
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
            Box(modifier = Modifier.fillMaxSize()) {
                TextButton(modifier = Modifier.align(Alignment.BottomCenter), onClick = {
                    viewModel.onEvent(RegisterEvent.OnLoginScreen)
                }) {
                    Text("Or Login with your Account")
                }
            }
        }
    }
}

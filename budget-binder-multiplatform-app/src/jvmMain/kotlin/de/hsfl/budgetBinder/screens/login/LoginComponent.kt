package de.hsfl.budgetBinder.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.compose.dialog.ServerUrlDialog
import de.hsfl.budgetBinder.compose.icon.AppIcon
import de.hsfl.budgetBinder.compose.textfield.EmailTextField
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.login.LoginViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.login.LoginEvent
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun LoginComponent() {
    val scope = rememberCoroutineScope()
    val viewModel: LoginViewModel by di.instance()
    val emailTextState = viewModel.emailText.collectAsState(scope.coroutineContext)
    val passwordTextState = viewModel.passwordText.collectAsState(scope.coroutineContext)
    val serverUrlState = viewModel.serverUrlText.collectAsState(scope.coroutineContext)
    val openDialog = viewModel.dialogState.collectAsState(scope.coroutineContext)
    val localFocusManager = LocalFocusManager.current
    val loadingState = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoginEvent.LifeCycle(LifecycleEvent.OnLaunch))
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                else -> loadingState.value = false
            }
        }
    }

    if (loadingState.value) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    ServerUrlDialog(
        value = serverUrlState.value.serverAddress,
        onValueChange = { viewModel.onEvent(LoginEvent.EnteredServerUrl(it)) },
        openDialog = openDialog.value,
        onConfirm = { viewModel.onEvent(LoginEvent.OnServerUrlDialogConfirm) },
        onDismiss = { viewModel.onEvent(LoginEvent.OnServerUrlDialogDismiss) }
    )
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppIcon(modifier = Modifier.size(128.dp).padding(8.dp))
        Text(text = "Welcome back to Budget Binder 💸", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(8.dp))
        EmailTextField(
            value = emailTextState.value.email,
            onValueChange = { viewModel.onEvent(LoginEvent.EnteredEmail(it)) },
            label = { Text("Email") },
            isError = !emailTextState.value.emailValid,
            enabled = !loadingState.value
        )
        OutlinedTextField(
            value = passwordTextState.value.password,
            onValueChange = { viewModel.onEvent(LoginEvent.EnteredPassword(it)) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        Button(onClick = {
            localFocusManager.clearFocus()
            viewModel.onEvent(LoginEvent.OnLogin)
        }) {
            Text("Login")
        }
        Box(modifier = Modifier.fillMaxSize()) {
            TextButton(modifier = Modifier.align(Alignment.BottomCenter), onClick = {
                localFocusManager.clearFocus()
                viewModel.onEvent(LoginEvent.OnRegisterScreen)
            }) {
                Text("Or Register your Account here")
            }
        }
    }
}

package de.hsfl.budgetBinder.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.EditUserEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEditUserViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun SettingsEditUserView(modifier: Modifier = Modifier, isLoading: Boolean) {
    val scope = rememberCoroutineScope()
    val viewModel: SettingsEditUserViewModel by di.instance()
    val firstNameText = viewModel.firstNameText.collectAsState()
    val lastNameText = viewModel.lastNameText.collectAsState(scope.coroutineContext)
    val emailText = viewModel.emailText.collectAsState(scope.coroutineContext)
    val passwordText = viewModel.passwordText.collectAsState(scope.coroutineContext)
    //val loadingState = remember { mutableStateOf(false) }

    /*LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.ShowError -> {
                    loadingState.value = false
                    scaffoldState.snackbarHostState.showSnackbar(message = event.msg)
                }
            }
        }
    }*/

    /*if (loadingState.value) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }*/
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        SettingsTextField(
            modifier = Modifier.padding(8.dp),
            value = firstNameText.value.firstName,
            onValueChange = { viewModel.onEvent(EditUserEvent.EnteredFirstName(it)) },
            label = { Text("Firstname") },
            isError = !firstNameText.value.firstNameIsValid,
            enabled = !isLoading,
            errorText = "Firstname can't be blank"
        )
        SettingsTextField(
            modifier = Modifier.padding(8.dp),
            value = lastNameText.value.lastName,
            onValueChange = { viewModel.onEvent(EditUserEvent.EnteredLastName(it)) },
            label = { Text("Lastname") },
            isError = !lastNameText.value.lastNameIsValid,
            enabled = !isLoading,
            errorText = "Lastname can't be blank"
        )
        SettingsTextField(
            modifier = Modifier.padding(8.dp),
            value = emailText.value,
            onValueChange = { },
            label = { Text("Email") },
            enabled = false,
        )
        SettingsTextField(
            modifier = Modifier.padding(8.dp),
            value = passwordText.value.password,
            onValueChange = { viewModel.onEvent(EditUserEvent.EnteredPassword(it)) },
            label = { Text("Password") },
            isError = !passwordText.value.passwordIsValid,
            enabled = !isLoading,
            errorText = "Password can't be blank"
        )
        Row {
            TextButton(onClick = { viewModel.onEvent(EditUserEvent.OnGoBack) }) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { viewModel.onEvent(EditUserEvent.OnUpdate) }) {
                Text("Update")
            }
        }
    }
}

@Composable
fun SettingsTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable () -> Unit,
    isError: Boolean = false,
    errorText: String = "",
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    Column {
        TextField(
            modifier = modifier,
            value = value,
            onValueChange = onValueChange,
            label = label,
            isError = isError,
            enabled = enabled,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            trailingIcon = {
                if (isError) {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
            }
        )
        if (isError) {
            Text(text = errorText, style = MaterialTheme.typography.subtitle1, color = MaterialTheme.colors.error)
        }
    }

}

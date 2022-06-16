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
import de.hsfl.budgetBinder.compose.textfield.SettingsTextField
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.EditUserEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEditUserViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun SettingsEditUserView(modifier: Modifier = Modifier, isLoading: Boolean) {
    val viewModel: SettingsEditUserViewModel by di.instance()
    val firstNameText = viewModel.firstNameText.collectAsState()
    val lastNameText = viewModel.lastNameText.collectAsState()
    val emailText = viewModel.emailText.collectAsState()
    val passwordText = viewModel.passwordText.collectAsState()

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        SettingsTextField(
            value = firstNameText.value.firstName,
            onValueChange = { viewModel.onEvent(EditUserEvent.EnteredFirstName(it)) },
            label = { Text("Firstname") },
            isError = !firstNameText.value.firstNameIsValid,
            enabled = !isLoading,
            errorText = "Firstname can't be blank"
        )
        Spacer(modifier = Modifier.height(8.dp))
        SettingsTextField(
            value = lastNameText.value.lastName,
            onValueChange = { viewModel.onEvent(EditUserEvent.EnteredLastName(it)) },
            label = { Text("Lastname") },
            isError = !lastNameText.value.lastNameIsValid,
            enabled = !isLoading,
            errorText = "Lastname can't be blank"
        )
        Spacer(modifier = Modifier.height(8.dp))
        SettingsTextField(
            value = emailText.value,
            onValueChange = { },
            label = { Text("Email") },
            enabled = false,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SettingsTextField(
            value = passwordText.value.password,
            onValueChange = { viewModel.onEvent(EditUserEvent.EnteredPassword(it)) },
            label = { Text("Password") },
            isError = !passwordText.value.passwordIsValid,
            enabled = !isLoading,
            errorText = "Password can't be blank"
        )
        Spacer(modifier = Modifier.height(8.dp))
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

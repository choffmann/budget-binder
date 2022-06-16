package de.hsfl.budgetBinder.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.compose.textfield.SettingsPasswordTextField
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
    val confirmedPasswordText = viewModel.confirmedPassword.collectAsState()
    val scrollState = rememberScrollState()

    // TODO: Fit the rest of TextField with Password Row
    Column(
        modifier = modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            //modifier = Modifier.width(IntrinsicSize.Min),
            value = lastNameText.value.lastName,
            onValueChange = { viewModel.onEvent(EditUserEvent.EnteredLastName(it)) },
            label = { Text("Lastname") },
            isError = !lastNameText.value.lastNameIsValid,
            enabled = !isLoading,
            errorText = "Lastname can't be blank"
        )
        Spacer(modifier = Modifier.height(8.dp))
        SettingsTextField(
            //modifier = Modifier.width(IntrinsicSize.Min),
            value = emailText.value,
            onValueChange = { },
            label = { Text("Email") },
            enabled = false,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SettingsPasswordTextField(
            spaceBetween = 8.dp,
            passwordText = passwordText.value.password,
            onPasswordChange = { viewModel.onEvent(EditUserEvent.EnteredPassword(it)) },
            confirmedPasswordText = confirmedPasswordText.value.confirmedPassword,
            onConfirmedPasswordText = { viewModel.onEvent(EditUserEvent.EnteredConfirmedPassword(it)) },
            passwordIsValid = passwordText.value.passwordIsValid,
            confirmedPasswordIsValid = confirmedPasswordText.value.confirmedPasswordIsValid,
            enabled = !isLoading
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

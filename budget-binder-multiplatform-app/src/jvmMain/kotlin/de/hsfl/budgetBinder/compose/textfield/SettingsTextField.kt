package de.hsfl.budgetBinder.compose.textfield

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.presentation.viewmodel.settings.EditUserEvent

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
            },
            singleLine = true
        )
        if (isError) {
            Text(text = errorText, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.error)
        }
    }
}

@Composable
fun SettingsPasswordTextField(
    modifier: Modifier = Modifier,
    spaceBetween: Dp = 8.dp,
    passwordText: String,
    onPasswordChange: (String) -> Unit,
    confirmedPasswordText: String,
    onConfirmedPasswordText: (String) -> Unit,
    passwordIsValid: Boolean,
    confirmedPasswordIsValid: Boolean,
    enabled: Boolean
) {
    BoxWithConstraints {
        if (maxWidth >= 600.dp) {
            Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
                SettingsTextField(
                    value = passwordText,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    isError = !passwordIsValid,
                    enabled = enabled,
                    errorText = "Password can't be blank",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.width(spaceBetween))
                SettingsTextField(
                    value = confirmedPasswordText,
                    onValueChange = onConfirmedPasswordText,
                    label = { Text("Confirm Password") },
                    isError = !confirmedPasswordIsValid,
                    enabled = enabled,
                    errorText = "Password didn't match",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        } else {
            Column (modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                SettingsTextField(
                    value = passwordText,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    isError = !passwordIsValid,
                    enabled = enabled,
                    errorText = "Password can't be blank",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(spaceBetween))
                SettingsTextField(
                    value = confirmedPasswordText,
                    onValueChange = onConfirmedPasswordText,
                    label = { Text("Confirm Password") },
                    isError = !confirmedPasswordIsValid,
                    enabled = enabled,
                    errorText = "Password didn't match",
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
            }
        }
    }
}

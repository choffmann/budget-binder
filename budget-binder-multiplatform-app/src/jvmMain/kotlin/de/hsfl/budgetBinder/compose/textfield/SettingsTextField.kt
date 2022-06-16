package de.hsfl.budgetBinder.compose.textfield

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

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
            Text(text = errorText, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.error)
        }
    }
}

package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.runtime.Composable

@Composable
actual fun ServerUrlDialog(
    value: String,
    onValueChange: (String) -> Unit,
    openDialog: Boolean,
    onConfirm: () -> Unit,
    onDissmiss: () -> Unit
) {
    if (openDialog) {
        AlertDialog(onDismissRequest = onDissmiss,
            title = { Text(text = "Please Enter Server URL") },
            text = {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text("Server URL") },
                    leadingIcon = { Icon(Icons.Filled.Dns, contentDescription = null) })
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = "Confirm")
                }
            })
    }
}
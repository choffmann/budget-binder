package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun ServerUrlDialog(
    value: String,
    onValueChange: (String) -> Unit,
    openDialog: Boolean,
    onConfirm: () -> Unit,
    onDissmiss: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            title = { Text(text = "Please Enter Server URL", style = MaterialTheme.typography.body2) },
            onDismissRequest = onDissmiss,
            buttons = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        modifier = Modifier.padding(bottom = 8.dp, end = 8.dp).align(Alignment.CenterEnd),
                        onClick = onConfirm
                    ) {
                        Text("Confirm")
                    }
                }
            },
            text = {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text("Server URL") },
                    leadingIcon = { Icon(Icons.Filled.Dns, contentDescription = null) },
                    singleLine = true
                )
            }
        )
    }
}
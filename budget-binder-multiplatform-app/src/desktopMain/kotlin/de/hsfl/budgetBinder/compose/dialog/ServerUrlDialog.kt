package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
actual fun ServerUrlDialog(
    value: String,
    onValueChange: (String) -> Unit,
    openDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (openDialog) {
        AlertDialog(
            title = { Text(text = "Please enter server url", style = MaterialTheme.typography.body2, fontWeight = FontWeight.Bold) },
            onDismissRequest = onDismiss,
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
                Column {
                    TextField(
                        value = value,
                        onValueChange = onValueChange,
                        label = { Text("Server URL") },
                        leadingIcon = { Icon(Icons.Filled.Dns, contentDescription = null) },
                        singleLine = true
                    )
                    Text("Please enter the server url from your own budget binder server here", style = MaterialTheme.typography.caption)
                }
            }
        )
    }
}

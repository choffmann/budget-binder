package de.hsfl.budgetBinder.prototype.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import de.hsfl.budgetBinder.android.R

@Composable
actual fun ServerUrlDialog(openDialog: MutableState<Boolean>, onConfirm: (String) -> Unit) {
    val serverUrlState = remember { mutableStateOf("http://localhost:8080") }
    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
            serverUrlState.value = "http://localhost:8080"
        },
            title = { Text(text = "Please Enter Server URL") },
            text = {
                TextField(value = serverUrlState.value,
                    onValueChange = { serverUrlState.value = it },
                    label = { Text("Server URL") },
                    leadingIcon = { Icon(Icons.Filled.Dns, contentDescription = null) })
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(serverUrlState.value)
                    openDialog.value = false
                }) {
                    Text(text = "Confirm")
                }
            })
    }

}

@Composable
actual fun AppIcon(modifier: Modifier) {
    val appIcon = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.ic_budgetbinder))
    Image(modifier = modifier, painter = appIcon, contentDescription = null)
}
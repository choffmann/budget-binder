package de.hsfl.budgetBinder.compose.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
actual fun ServerUrlDialog(openDialog: MutableState<Boolean>, onConfirm: (String) -> Unit) {
    val serverUrlTextState = remember { mutableStateOf("https://bb-server.fpcloud.de/") }
    if (openDialog.value) {
        Box(modifier = Modifier.fillMaxSize().background(Color.LightGray.copy(alpha = 0.8f))) {
            Card(modifier = Modifier.align(Alignment.Center)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = serverUrlTextState.value,
                        onValueChange = { serverUrlTextState.value = it },
                        label = { Text("Please Enter Server URL") },
                        leadingIcon = { Icon(Icons.Default.Dns, contentDescription = null) }
                    )
                    Row(modifier = Modifier.align(Alignment.End)) {
                        TextButton(
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 16.dp),
                            onClick = { openDialog.value = false }) {
                            Text("Dismiss")
                        }
                        Button(
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            onClick = {
                                openDialog.value = false
                                onConfirm(serverUrlTextState.value)
                            }) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}

@Composable
actual fun AppIcon(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("icon.png"), contentDescription = null)
}
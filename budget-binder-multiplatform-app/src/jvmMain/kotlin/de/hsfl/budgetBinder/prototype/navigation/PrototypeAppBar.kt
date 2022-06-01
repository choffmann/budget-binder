package de.hsfl.budgetBinder.prototype.navigation

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable

@Composable
fun PrototypeAppBar() {
    TopAppBar(title = { Text("Budget Binder Prototype") })
}

@Composable
fun PrototypeAppBar(onMenuClicked: () -> Unit) {
    TopAppBar(title = { Text("Budget Binder Prototype") }, navigationIcon = {
        IconButton(onClick = { onMenuClicked() }) {
            Icon(Icons.Filled.Menu, contentDescription = null)
        }
    })
}
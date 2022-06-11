package de.hsfl.budgetBinder.compose.navigation

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable

@Composable
internal fun AppBarComponent() {
    TopAppBar(title = { Text("Budget Binder") })
}

@Composable
internal fun AppBarComponent(onMenuClicked: () -> Unit) {
    TopAppBar(title = { Text("Budget Binder") }, navigationIcon = {
        IconButton(onClick = { onMenuClicked() }) {
            Icon(Icons.Filled.Menu, contentDescription = null)
        }
    })
}
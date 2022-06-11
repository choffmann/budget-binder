package de.hsfl.budgetBinder.compose.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.compose.StateManager.screenState
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.compose.StateManager.darkMode
import de.hsfl.budgetBinder.compose.StateManager.drawerState
import de.hsfl.budgetBinder.compose.StateManager.userState
import kotlinx.coroutines.launch


@Composable
internal fun DrawerContent() {
    Column {
        DrawerUser()
        Divider()
        DrawerList()
    }
}

@Composable
private fun DrawerUser() {
    // TODO: Replace with better Icon for User
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(modifier = Modifier.size(48.dp), imageVector = Icons.Filled.AccountCircle, contentDescription = null)
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = "${userState.value.firstName} ${userState.value.name}", style = MaterialTheme.typography.h6
            )
            Text(
                text = userState.value.email, style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DrawerList() {
    val scope = rememberCoroutineScope()
    Column {
        ListItem(modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = {
            screenState.value = Screen.Home
            scope.launch { drawerState.close() }
        }), text = { Text("Übersicht") }, icon = { Icon(Icons.Filled.Home, contentDescription = null) })
        ListItem(modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = {
            screenState.value = Screen.Categories
            scope.launch { drawerState.close() }
        }), text = { Text("Kategorien") }, icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) })
        ListItem(modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = {
            screenState.value = Screen.Settings
            scope.launch { drawerState.close() }
        }), text = { Text("Einstellungen") }, icon = { Icon(Icons.Filled.Settings, contentDescription = null) })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(modifier = Modifier.padding(start = 8.dp),
                checked = darkMode.value,
                onCheckedChange = { darkMode.value = it })
            Text(modifier = Modifier.padding(start = 16.dp), text = "Darkmode")
        }
    }
}
package de.hsfl.budgetBinder.prototype.navigation

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
import de.hsfl.budgetBinder.prototype.StateManager.screenState
import de.hsfl.budgetBinder.prototype.PrototypeScreen
import de.hsfl.budgetBinder.prototype.StateManager.darkMode
import de.hsfl.budgetBinder.prototype.StateManager.drawerState
import kotlinx.coroutines.launch


@Composable
fun DrawerContent() {
    Column {
        DrawerUser()
        Divider()
        DrawerList()
    }
}

@Composable
private fun DrawerUser() {
    // TODO: Replace with better Icon for User
    val painter: Painter = rememberVectorPainter(Icons.Filled.AccountCircle)
    Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(modifier = Modifier.size(48.dp), painter = painter, contentDescription = null)
        Spacer(modifier = Modifier.size(8.dp))
        Column {
            Text(
                text = "Hoffmann, Cedrik", style = MaterialTheme.typography.h6
            )
            Text(
                text = "hoffmann@mail.com", style = MaterialTheme.typography.subtitle1
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
            screenState.value = PrototypeScreen.Home
            scope.launch { drawerState.close() }
        }), text = { Text("Übersicht") }, icon = { Icon(Icons.Filled.Home, contentDescription = null) })
        ListItem(modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = {
            screenState.value = PrototypeScreen.Categories
            scope.launch { drawerState.close() }
        }), text = { Text("Kategorien") }, icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) })
        ListItem(modifier = Modifier.align(Alignment.CenterHorizontally).clickable(onClick = {
            screenState.value = PrototypeScreen.Settings
            scope.launch { drawerState.close() }
        }), text = { Text("Einstellungen") }, icon = { Icon(Icons.Filled.Info, contentDescription = null) })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(modifier = Modifier.padding(start = 8.dp),
                checked = darkMode.value,
                onCheckedChange = { darkMode.value = it })
            Text(modifier = Modifier.padding(start = 16.dp), text = "Darkmode")
        }
    }
}
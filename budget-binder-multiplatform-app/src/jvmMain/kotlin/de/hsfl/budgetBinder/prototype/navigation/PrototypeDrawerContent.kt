package de.hsfl.budgetBinder.prototype

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp


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
    Column {
        ListItem(modifier = Modifier.align(Alignment.CenterHorizontally).clickable(
            onClick = { /* ScreenState = Home */ }
        ),
            text = { Text("Übersicht") },
            icon = { Icon(Icons.Filled.Home, contentDescription = null) })
        ListItem(modifier = Modifier.align(Alignment.CenterHorizontally).clickable(
            onClick = { /* ScreenState = Categories */ }
        ),
            text = { Text("Kategorien") },
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) })
        ListItem(modifier = Modifier.align(Alignment.CenterHorizontally).clickable(
            onClick = { /* ScreenState = Settings */ }
        ),
            text = { Text("Einstellungen") },
            icon = { Icon(Icons.Filled.Info, contentDescription = null) })
    }
}
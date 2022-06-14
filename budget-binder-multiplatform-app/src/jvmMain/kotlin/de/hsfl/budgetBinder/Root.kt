package de.hsfl.budgetBinder

import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import de.hsfl.budgetBinder.di.kodein
import io.ktor.client.engine.cio.*
import org.kodein.di.compose.withDI

val di = kodein(ktorEngine = CIO.create())

@Composable
fun App() = withDI(di) {
    val darkTheme = remember { mutableStateOf(false) }
    MaterialTheme(
        colors = if (darkTheme.value) darkColors() else lightColors()
    ) {
        Router()

        // Toggle Dark-mode
        IconToggleButton(checked = darkTheme.value, onCheckedChange = { darkTheme.value = it }) {
            if (darkTheme.value)
                Icon(Icons.Filled.Info, contentDescription = null, tint = Color.White)
            else
                Icon(Icons.Filled.Info, contentDescription = null, tint = Color.Black)
        }
    }
}
package de.hsfl.budgetBinder

import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import de.hsfl.budgetBinder.di.kodein
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import io.ktor.client.engine.cio.*
import org.kodein.di.compose.withDI
import org.kodein.di.instance

val di = kodein(ktorEngine = CIO.create())

@Composable
fun App() = withDI(di) {
    val scope = rememberCoroutineScope()
    val dataFlow: DataFlow by di.instance()
    val darkTheme = dataFlow.darkModeState.collectAsState(scope.coroutineContext)
    val scaffoldState = rememberScaffoldState()
    MaterialTheme(
        colors = if (darkTheme.value) darkColors() else lightColors()
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                // TODO: Show NavDrawer not on Login und Register
                TopAppBar(title = { Text("Budget Binder") }, navigationIcon = {
                    IconButton(onClick = {
                        // TODO: Implement NavBar Logic
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                })
            }
        ) {
            Router()
        }
    }
}

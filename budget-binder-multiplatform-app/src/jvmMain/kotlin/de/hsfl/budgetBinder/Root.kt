package de.hsfl.budgetBinder

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.di.kodein
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.compose.withDI
import org.kodein.di.instance

val di = kodein(ktorEngine = CIO.create())

@Composable
fun App() = withDI(di) {
    val scope = rememberCoroutineScope()
    val dataFlow: DataFlow by di.instance()
    val uiEventFlow: UiEventSharedFlow by di.instance()
    val darkTheme = dataFlow.darkModeState.collectAsState(scope.coroutineContext)
    val scaffoldState = rememberScaffoldState()
    val loadingState = remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    LaunchedEffect(key1 = true) {
        uiEventFlow.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.ShowError -> {
                    loadingState.value = false
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.msg,
                        actionLabel = "Dismiss"
                    )
                }
                is UiEvent.ShowSuccess -> {
                    loadingState.value = false
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.msg,
                        actionLabel = "Dismiss"
                    )
                }
                else -> {}
            }
        }
    }

    MaterialTheme(
        colors = if (darkTheme.value) darkColors() else lightColors()
    ) {
        if (loadingState.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                // TODO: Show NavDrawer not on Login und Register
                TopAppBar(title = { Text("Budget Binder") }, navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            if (drawerState.isOpen) drawerState.close()
                            else drawerState.open()
                        }
                    }) {
                        Icon(Icons.Filled.Menu, contentDescription = null)
                    }
                })
            }
        ) {
            NavDrawer(drawerState = drawerState) {
                Router()
            }
        }
    }
}

package de.hsfl.budgetBinder.prototype

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

// Root Component to handle View's
@Composable
fun Prototype() {
    val scaffoldState = rememberScaffoldState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val darkTheme = remember { mutableStateOf(false) }
    MaterialTheme(
        colors = if (darkTheme.value) darkColors() else lightColors()
    ) {
        // Scaffold to handle AppBar, Floating Buttons, Snackbar and Co.
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { PrototypeAppBar { scope.launch { drawerState.open() } } },
            snackbarHost = {},
            floatingActionButton = {},
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = false,
        ) {
            ModalDrawer(drawerState = drawerState, drawerContent = { DrawerContent() }, content = { Router() })
        }
    }

}
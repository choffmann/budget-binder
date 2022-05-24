package de.hsfl.budgetBinder.prototype

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import de.hsfl.budgetBinder.prototype.StateManager.darkMode
import de.hsfl.budgetBinder.prototype.StateManager.drawerState
import de.hsfl.budgetBinder.prototype.navigation.DrawerContent
import kotlinx.coroutines.launch

// Root Component to handle View's
@Composable
fun Prototype() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    MaterialTheme(
        colors = if (darkMode.value) darkColors() else lightColors()
    ) {
        // Scaffold to handle AppBar, Floating Buttons, Snackbar and Co.
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                PrototypeAppBar {
                    scope.launch {
                        if (drawerState.isOpen) drawerState.close()
                        else drawerState.open()
                    }
                }
            },
            snackbarHost = {},
            floatingActionButton = {},
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = false,
        ) {
            ModalDrawer(drawerState = drawerState, drawerContent = { DrawerContent() }, content = { Router() })
        }
    }

}
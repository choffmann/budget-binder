package de.hsfl.budgetBinder.prototype

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import de.hsfl.budgetBinder.prototype.StateManager.darkMode
import de.hsfl.budgetBinder.prototype.StateManager.drawerState
import de.hsfl.budgetBinder.prototype.StateManager.isLoggedIn
import de.hsfl.budgetBinder.prototype.StateManager.scaffoldState
import de.hsfl.budgetBinder.prototype.navigation.DrawerContent
import de.hsfl.budgetBinder.prototype.navigation.PrototypeAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Root Component to handle View's
@Composable
fun Prototype() {
    val scaffold = remember { scaffoldState }
    val scope = rememberCoroutineScope()
    MaterialTheme(
        colors = if (darkMode.value) darkColors() else lightColors()
    ) {
        // Scaffold to handle AppBar, Floating Buttons, Snackbar and Co.
        Scaffold(
            scaffoldState = scaffold,
            topBar = {
                if (!isLoggedIn.value) PrototypeAppBar()
                else PrototypeAppBar(onMenuClicked = { toggleDrawerNav(scope) })
            },
            floatingActionButton = {},
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = false,
        ) {
            ModalDrawer(drawerState = drawerState,
                gesturesEnabled = false,
                drawerContent = { DrawerContent() },
                content = { Router() })
        }
    }
}

private fun toggleDrawerNav(scope: CoroutineScope) {
    scope.launch {
        if (drawerState.isOpen) drawerState.close()
        else drawerState.open()
    }
}
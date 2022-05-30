package de.hsfl.budgetBinder.prototype

import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf

object StateManager {
    val screenState = mutableStateOf<PrototypeScreen>(PrototypeScreen.Welcome)
    val drawerState = DrawerState(DrawerValue.Closed)
    val snackbarHostState = SnackbarHostState()
    val scaffoldState = ScaffoldState(drawerState = drawerState, snackbarHostState = snackbarHostState)

    // Settings
    val darkMode = mutableStateOf(false)
    val userState = mutableStateOf(User())
    val serverState = mutableStateOf(Server())
}
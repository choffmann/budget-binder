package de.hsfl.budgetBinder.prototype

import androidx.compose.material.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

object StateManager {
    val screenState = mutableStateOf<PrototypeScreen>(PrototypeScreen.FTUX)
    val drawerState = DrawerState(DrawerValue.Closed)
    val snackbarHostState = SnackbarHostState()
    val scaffoldState = ScaffoldState(drawerState = drawerState, snackbarHostState = snackbarHostState)
    val isLoggedIn = mutableStateOf(true)

    // Settings
    val darkMode = mutableStateOf(false)
    val userState = mutableStateOf(User())
    val serverState = mutableStateOf(Server())

    // Categories
    val selectedCategories = mutableStateListOf<Category>()
}
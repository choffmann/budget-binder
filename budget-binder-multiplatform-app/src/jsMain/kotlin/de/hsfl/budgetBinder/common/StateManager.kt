package de.hsfl.budgetBinder.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import de.hsfl.budgetBinder.common.Constants.BASE_URL
import de.hsfl.budgetBinder.presentation.Screen

object StateManager {
    // TODO: screenState replaced with new Router
    //val screenState = mutableStateOf<Screen>(Screen.Welcome)
    //val drawerState = DrawerState(DrawerValue.Closed)
    //val snackbarHostState = SnackbarHostState()
    //val scaffoldState = ScaffoldState(drawerState = drawerState, snackbarHostState = snackbarHostState)
    val isLoggedIn = mutableStateOf(false)


    // Settings
    val darkMode = mutableStateOf(false)
    val userState = mutableStateOf(User(0, "", "", ""))
    val serverState = mutableStateOf(BASE_URL)

}
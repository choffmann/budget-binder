package de.hsfl.budgetBinder.prototype

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.runtime.mutableStateOf

object StateManager {
    val screenState = mutableStateOf<PrototypeScreen>(PrototypeScreen.Welcome)
    val drawerState = DrawerState(DrawerValue.Closed)
    val darkMode = mutableStateOf(false)
    val userState = mutableStateOf(User())
    val server = mutableStateOf(Server())
}
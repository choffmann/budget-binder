package de.hsfl.budgetBinder.prototype

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.runtime.mutableStateOf

object StateManager {
    val screenState = mutableStateOf<PrototypeScreen>(PrototypeScreen.Welcome)
    val darkMode = mutableStateOf(false)
    val drawerState = DrawerState(DrawerValue.Closed)
}
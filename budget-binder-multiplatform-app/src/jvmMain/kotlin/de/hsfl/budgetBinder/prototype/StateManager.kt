package de.hsfl.budgetBinder.prototype

import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.runtime.mutableStateOf

object StateManager {
    val screenState = mutableStateOf<PrototypeScreen>(PrototypeScreen.Screen1)
    val darkMode = mutableStateOf(false)
    val drawerState = DrawerState(DrawerValue.Closed)
}
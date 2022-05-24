package de.hsfl.budgetBinder.prototype

import androidx.compose.runtime.mutableStateOf

object StateManager {
    val screenState = mutableStateOf<PrototypeScreen>(PrototypeScreen.Screen1)
}
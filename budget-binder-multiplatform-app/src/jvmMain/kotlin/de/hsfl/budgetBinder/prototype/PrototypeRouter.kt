package de.hsfl.budgetBinder.prototype

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.prototype.Constants.screenState
import de.hsfl.budgetBinder.prototype.screen1.Screen1Component
import de.hsfl.budgetBinder.prototype.screen2.Screen2Component

@Composable
fun Router() {
    when (screenState.value) {
        is PrototypeScreen.Screen1 -> Screen1Component()
        is PrototypeScreen.Screen2 -> Screen2Component()
    }
}
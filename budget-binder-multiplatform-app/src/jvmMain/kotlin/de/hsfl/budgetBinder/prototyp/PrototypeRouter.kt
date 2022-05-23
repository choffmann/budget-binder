package de.hsfl.budgetBinder.prototyp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import de.hsfl.budgetBinder.prototyp.screen1.Screen1Component
import de.hsfl.budgetBinder.prototyp.screen2.Screen2Component

@Composable
fun Router(screenState: MutableState<PrototypeScreen>) {
    when (screenState.value) {
        is PrototypeScreen.Screen1 -> {
            Screen1Component(screenState)
        }
        is PrototypeScreen.Screen2 -> {
            Screen2Component(screenState)
        }
    }
}
package de.hsfl.budgetBinder.prototype.screens.screen2

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.prototype.StateManager.screenState
import de.hsfl.budgetBinder.prototype.PrototypeScreen

@Composable
fun Screen2Component() {
    Screen2View(msg = (screenState.value as PrototypeScreen.Screen2).msg) {
        screenState.value = PrototypeScreen.Screen1
    }
}

@Composable
private fun Screen2View(msg: String, onButtonClicked: () -> Unit) {
    Column {
        Text(msg)
        Button(onClick = { onButtonClicked() }) {
            Text("Back")
        }
    }
}
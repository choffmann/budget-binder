package de.hsfl.budgetBinder.prototyp.screen1

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import de.hsfl.budgetBinder.prototyp.PrototypeScreen

@Composable
fun Screen1Component(screenState: MutableState<PrototypeScreen>) {
    Screen1View { msg ->
        screenState.value = PrototypeScreen.Screen2(msg)
    }
}

@Composable
fun Screen1View(onButtonClicked: (String) -> Unit) {
    val msgTextState = remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = msgTextState.value,
            onValueChange = { msgTextState.value = it },
            label = { Text("Message") },
            singleLine = true
        )
        Button(onClick = {
            onButtonClicked(msgTextState.value)
            msgTextState.value = ""
        }) {
            Text("Send")
        }
    }
}
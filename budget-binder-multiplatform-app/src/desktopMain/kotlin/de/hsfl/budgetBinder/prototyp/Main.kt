package de.hsfl.budgetBinder.prototyp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Budget Binder (Prototype)") {
        Prototype()
    }
}
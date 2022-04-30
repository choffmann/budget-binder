package de.hsfl.budgetBinder.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.hsfl.budgetBinder.compose.App


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
package de.hsfl.budgetBinder.desktop

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.hsfl.budgetBinder.ApplicationFlow
import de.hsfl.budgetBinder.UIState
import de.hsfl.budgetBinder.client.Client
import de.hsfl.budgetBinder.compose.ApplicationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        ApplicationView(painterResource("svg/hello_world.svg"))
    }
}
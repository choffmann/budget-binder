package de.hsfl.budgetBinder.desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.badoo.reaktive.scheduler.overrideSchedulers
import com.badoo.reaktive.coroutinesinterop.asScheduler
import kotlinx.coroutines.Dispatchers
import de.hsfl.budgetBinder.compose.App
import de.hsfl.budgetBinder.presentation.component.root.AppRoot
import de.hsfl.budgetBinder.presentation.component.root.RootComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalDecomposeApi::class)
fun main() {
    overrideSchedulers(main = Dispatchers.Main::asScheduler)
    val lifecycle = LifecycleRegistry()
    val root = root(DefaultComponentContext(lifecycle = lifecycle))

    application {
        val windowState = rememberWindowState()
        LifecycleController(lifecycle, windowState)
        Window(
            onCloseRequest = ::exitApplication,
            title = "Budget Binder",
            state = windowState
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                MaterialTheme {
                    App(root)
                }
            }
        }
    }
}

private fun root(componentContext: ComponentContext): AppRoot =
    RootComponent(componentContext = componentContext)
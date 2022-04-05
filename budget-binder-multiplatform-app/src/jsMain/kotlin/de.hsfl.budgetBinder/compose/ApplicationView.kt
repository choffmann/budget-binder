package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import de.hsfl.budgetBinder.common.*

@Composable
fun ApplicationView() {
    Div(
        attrs = {
            classes("mdc-typography--headline4", "hello-world")
            style {
                //TODO: Style in Compose
            }
        }
    ) {
        Text(HelloWorld().msg)
        Div(
            attrs = {
                classes("mdc-typography--subtitle1")
                style {
                    //TODO: Style in Compose
                }
            }
        ) { Text("from ${Platform().platform}") }
    }
}
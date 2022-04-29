package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text


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
        Text("Hello World!")
        Div(
            attrs = {
                classes("mdc-typography--subtitle1")
                style {
                    //TODO: Style in Compose
                }
            }
        ) {
            Text("from Kotlin JS")
        }
    }
}
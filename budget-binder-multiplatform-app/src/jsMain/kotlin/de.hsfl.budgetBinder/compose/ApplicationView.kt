package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.hsfl.budgetBinder.ApplicationFlow
import de.hsfl.budgetBinder.UIState
import de.hsfl.budgetBinder.client.Client
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import de.hsfl.budgetBinder.model.*
import kotlinx.coroutines.MainScope


@Composable
fun ApplicationView() {
    val applicationFlow = ApplicationFlow(Client(), MainScope())
    val uiState by applicationFlow.uiState.collectAsState(MainScope())
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
        ) {
            Text("from ${Platform().platform}")
            if(uiState is UIState.Success) {
                Text((uiState as UIState.Success).users[0].name)
            }
        }
    }
}
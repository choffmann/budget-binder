package de.hsfl.budgetBinder.compose.settings

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.topBarMain
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.*


@Composable
fun SettingsView(
    state: State<Any>,
    onChangeToDashboard: () -> Unit,
    onChangeToSettings: () -> Unit,
    onChangeToCategory: () -> Unit,
    onDeleteButtonPressed: () -> Unit,
    onChangeButtonPressed: () -> Unit
) {
    val viewState by remember { state }

    topBarMain(
        logoButton = {
            Img(
                src = "images/Logo.png", alt = "Logo", attrs = {
                    classes("mdc-icon-button", AppStylesheet.image)
                    onClick { onChangeToDashboard() }
                }
            )
        }, navButtons = {
            Button(
                attrs = {
                    classes("mdc-button", "mdc-button--raised", "mdc-top-app-bar__navigation-icon")
                    onClick { onChangeToCategory() }
                }
            ) {
                Span(
                    attrs = {
                        classes("mdc-button__label")
                    }
                ) {
                    Text("Categories")
                }
            }
            Button(
                attrs = {
                    classes("mdc-button", "mdc-button--raised", "mdc-top-app-bar__navigation-icon")
                    onClick { onChangeToSettings() }
                }
            ) {
                Span(
                    attrs = {
                        classes("mdc-button__label")
                    }
                ) {
                    Text("Settings")
                }
            }
        })

    MainFlexContainer {
        Div(attrs = {
            classes("mdc-card", AppStylesheet.card)
        }
        ) {
            H1(
                attrs = {
                    style { marginLeft(2.percent) }
                }
            ) { Text("Settings") }
            when (viewState) {
                is UiState.Success<*> -> {
                    Text((viewState as UiState.Success<*>).element.toString())
                }
                is UiState.Error -> {
                    Text((viewState as UiState.Error).error)
                }
                is UiState.Loading -> {
                    //CircularProgressIndicator()
                }
            }
            Div(
                attrs = {
                    classes(AppStylesheet.margin, AppStylesheet.flexContainer)
                }
            ) {
                Button(
                    attrs = {
                        classes("mdc-button", "mdc-button--raised")
                        onClick { onChangeButtonPressed() }
                        style { flex(100.percent) }
                    }
                ) {
                    Text("Change Userdata")
                }
            }
            Div(
                attrs = {
                    classes(AppStylesheet.margin, AppStylesheet.flexContainer)
                }
            ) {
                Button(
                    attrs = {
                        classes("mdc-button", "mdc-button--raised")
                        onClick { onDeleteButtonPressed() }
                        style { flex(100.percent) }
                    }
                ) {
                    Text("Delete User")
                }
            }
        }
    }
}

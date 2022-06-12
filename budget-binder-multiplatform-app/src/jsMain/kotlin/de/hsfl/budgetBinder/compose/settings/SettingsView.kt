package de.hsfl.budgetBinder.compose.settings

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.topBarMain
import io.ktor.http.LinkHeader.Parameters.Title
import org.jetbrains.compose.web.dom.*


@Composable
fun SettingsView(
    state: State<Any>,
    onChangeToDashboard: () -> Unit,
    onChangeToSettings: () -> Unit,
    onChangeToCategory: () -> Unit
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
        H1 { Text("SettingsView") }
        Div(attrs = {
            classes("mdc-card", AppStylesheet.card)
        }) {
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
            Button(attrs = {
                onClick { onChangeToDashboard() }
            }) {
                Text("Back to Dashboard")
            }
        }
    }
}

fun changeDataDialog(){
    Div(
        attrs = {
            classes("mdc-dialog", "mdc-dialog--open", "mdc-dialog--fullscreen")
        }
    ) {
        Div(
            attrs = {
                classes("mdc-dialog__container")
            }
        ) {
            Div(
                attrs = {
                    classes("mdc-dialog__surface")
                    role="dialog"
                    aria-modal="true"
                    aria-labelledby="my-dialog-title"
                    aria-describedby="my-dialog-content"
                }
            ) {
                Div(
                    attrs = {
                        classes("mdc-dialog__header")
                    }
                ) {
                    H2(
                        attrs = {
                            classes("mdc-dialog__title")
                            id("my-dialog-title")
                        }
                    ) { Text("Dialog") }
                    Button(
                        attrs = {
                            classes("mdc-icon-button", "material-icons", "mdc-dialog__close")
                            data-mdc-dialog-action="close"
                        }
                    ) {
                        Text("Close")
                    }
                }
                Div(
                    attrs = {
                        classes("mdc-dialog__content")
                        id("my-dialog-content")
                    }
                ) {
                    Text("SSSSSSSSSSSSSSSSSSSSSSSSSSS")
                }
                Div(
                    attrs = {
                        classes("mdc-dialog__actions")
                    }
                ) {
                    Button(
                        attrs = {
                            classes("mdc-button", "mdc-dialog__button")
                            data-mdc-dialog-action="ok"
                        }
                    ) {
                        Div(
                            attrs = {
                                classes("mdc-button__ripple")
                            }
                        ) {
                            Span(
                                attrs = {
                                    classes("mdc-button__label")
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
        Div(
            attrs = {
                classes("mdc-dialog__scrim")
            }
        ) {

        }
    }
}

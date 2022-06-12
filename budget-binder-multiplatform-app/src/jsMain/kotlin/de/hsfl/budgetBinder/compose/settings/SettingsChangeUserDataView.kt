package de.hsfl.budgetBinder.compose.settings

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.topBarMain
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*


@Composable
fun SettingsChangeUserDataView(
    state: State<Any>,
    onChangeToDashboard: () -> Unit,
    onChangeToSettings: () -> Unit,
    onChangeToCategory: () -> Unit,
    onChangeDataButtonPressed: (firstName: String, lastName: String, password: String) -> Unit
) {
    var firstNameTextFieldState by remember { mutableStateOf("") }
    var lastNameTextFieldState by remember { mutableStateOf("") }
    var passwordTextFieldState by remember { mutableStateOf("") }
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
        Div(
            attrs = {
                classes("mdc-card", AppStylesheet.card)
            }
        ) {

            H1 { Text("Change User Data") }
            Form(attrs = {
                this.addEventListener("submit") {
                    console.log("$firstNameTextFieldState, $lastNameTextFieldState, $passwordTextFieldState")
                    onChangeDataButtonPressed(
                        firstNameTextFieldState,
                        lastNameTextFieldState,
                        passwordTextFieldState
                    )
                    it.preventDefault()
                }
            }
            ) {
                Div(
                    attrs = {
                        classes(AppStylesheet.margin)
                    }
                ) {
                    Label(
                        attrs = {
                            classes("mdc-text-field", "mdc-text-field--filled")
                            style { width(100.percent) }
                        }
                    ) {
                        Span(
                            attrs = {
                                classes("mdc-text-field__ripple")
                            }
                        ) { }
                        Span(
                            attrs = {
                                classes("mdc-floating-label", "mdc-floating-label--float-above")
                            }
                        ) { Text("Firstname") }
                        Input(
                            type = InputType.Text
                        ) {
                            classes("mdc-text-field__input")
                            value(firstNameTextFieldState)
                            onInput {
                                firstNameTextFieldState = it.value
                            }
                        }
                        Span(
                            attrs = {
                                classes("mdc-line-ripple")
                            }
                        ) { }
                    }
                }
                Div(
                    attrs = {
                        classes(AppStylesheet.margin)
                    }
                ) {
                    Label(
                        attrs = {
                            classes("mdc-text-field", "mdc-text-field--filled")
                            style { width(100.percent) }
                        }
                    ) {
                        Span(
                            attrs = {
                                classes("mdc-text-field__ripple")
                            }
                        ) { }
                        Span(
                            attrs = {
                                classes("mdc-floating-label", "mdc-floating-label--float-above")
                            }
                        ) { Text("Lastname") }
                        Input(
                            type = InputType.Text
                        ) {
                            classes("mdc-text-field__input")
                            value(lastNameTextFieldState)
                            onInput {
                                lastNameTextFieldState = it.value
                            }
                        }
                        Span(
                            attrs = {
                                classes("mdc-line-ripple")
                            }
                        ) { }
                    }
                }
                Div(
                    attrs = {
                        classes(AppStylesheet.margin)
                    }
                ) {
                    Label(
                        attrs = {
                            classes("mdc-text-field", "mdc-text-field--filled")
                            style { width(100.percent) }
                        }
                    ) {
                        Span(
                            attrs = {
                                classes("mdc-text-field__ripple")
                            }
                        ) { }
                        Span(
                            attrs = {
                                classes("mdc-floating-label", "mdc-floating-label--float-above")
                            }
                        ) { Text("Password") }
                        PasswordInput(value = passwordTextFieldState,
                            attrs = {
                                classes("mdc-text-field__input")
                                onInput {
                                    passwordTextFieldState = it.value
                                }
                            })
                        Span(
                            attrs = {
                                classes("mdc-line-ripple")
                            }
                        ) { }
                    }
                }
                Div(
                    attrs = {
                        classes(AppStylesheet.margin)
                    }
                ) {
                    SubmitInput(
                        attrs = {
                            classes("mdc-button", "mdc-button--raised")
                            value("Submit")
                        })
                }
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
            }
        }
    }
}

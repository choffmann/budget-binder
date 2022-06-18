package de.hsfl.budgetBinder.compose.login

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun LoginView(
    state: State<Any>,
    onLoginButtonPressed: (email: String, password: String) -> Unit,
    onLoginSuccess: () -> Unit,
    onChangeToRegister: () -> Unit
) {
    var emailTextFieldState by remember { mutableStateOf("") }
    var passwordTextFieldState by remember { mutableStateOf("") }
    val viewState by remember { state }

    Header(
        attrs = {
            classes("mdc-top-app-bar")
        }
    ) {
        Div(
            attrs = {
                classes("mdc-top-app-bar__row")
            }
        ) {
            Section(
                attrs = {
                    classes("mdc-top-app-bar__section", "mdc-top-app-bar__section--align-start")
                }
            ) {
                Img(
                    src = "images/Logo.png", alt = "Logo", attrs = {
                        classes("mdc-icon-button", AppStylesheet.image)
                    }
                )
                Span(
                    attrs = {
                        classes("mdc-top-app-bar__title")
                    }
                ) {
                    Text("Budget-Binder")
                }
            }
            Section(
                attrs = {
                    classes("mdc-top-app-bar__section", "mdc-top-app-bar__section--align-end")
                }
            ) {
                Button(
                    attrs = {
                        classes("mdc-button", "mdc-button--raised", "mdc-top-app-bar__navigation-icon")
                        onClick { onChangeToRegister() }
                    }
                ) {
                    Span(
                        attrs = {
                            classes("mdc-button__label")
                        }
                    ) {
                        Text("Register Instead")
                    }
                }
            }
        }
    }

    MainFlexContainer {
        // -- Login Form --
        H1 { Text(" Login") }
        Form(
            attrs = {
                this.addEventListener("submit") {
                    console.log("${emailTextFieldState}, ${passwordTextFieldState}")
                    onLoginButtonPressed(emailTextFieldState, passwordTextFieldState)
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
                    ) { Text("Email") }
                    EmailInput(value = emailTextFieldState,
                        attrs = {
                            classes("mdc-text-field__input")
                            onInput {
                                emailTextFieldState = it.value

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
            // -- Login Request Management --
            when (viewState) {
                is UiState.Success<*> -> {
                    onLoginSuccess()
                }
                is UiState.Error -> {
                    Text((viewState as UiState.Error).error)
                }
                is UiState.Loading -> {
                    //CircularProgressIndicator()
                }
                else -> {}
            }
        }
    }
}
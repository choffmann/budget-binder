package de.hsfl.budgetBinder.compose.register


import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*

@Composable
fun RegisterView(
    state: State<Any>,
    onRegisterButtonPressed: (firstName: String, lastName: String, email: String, password: String) -> Unit,
    onChangeToLogin: () -> Unit
) {
    var firstNameTextFieldState by remember { mutableStateOf("") }
    var lastNameTextFieldState by remember { mutableStateOf("") }
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
                        onClick { onChangeToLogin() }
                    }
                ) {
                    Span(
                        attrs = {
                            classes("mdc-button__label")
                        }
                    ) {
                        Text("Login Instead")
                    }
                }
            }
        }
    }

    MainFlexContainer{
        // -- Register Form --
        Div(
            attrs = {
                classes("mdc-card", AppStylesheet.card)
            }
        ) {

                H1 { Text(" Register") }
                Form(attrs = { //Probably possible with just a button OnClick instead of Form&Submit
                    this.addEventListener("submit") {
                        console.log("$firstNameTextFieldState, $lastNameTextFieldState, $emailTextFieldState, $passwordTextFieldState")
                        onRegisterButtonPressed(
                            firstNameTextFieldState,
                            lastNameTextFieldState,
                            emailTextFieldState,
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
                    // -- Register Request Management --
                    when (viewState) {
                        is UiState.Success<*> -> {
                            onChangeToLogin()
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
}
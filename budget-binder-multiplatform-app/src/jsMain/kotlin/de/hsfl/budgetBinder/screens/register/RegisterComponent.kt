package de.hsfl.budgetBinder.screens.register

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.auth.register.RegisterEvent
import de.hsfl.budgetBinder.presentation.viewmodel.auth.register.RegisterViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun RegisterComponent() {
    val viewModel: RegisterViewModel by di.instance()
    val firstNameTextState = viewModel.firstNameText.collectAsState()
    val lastNameTextState = viewModel.lastNameText.collectAsState()
    val emailTextState = viewModel.emailText.collectAsState()
    val passwordTextState = viewModel.passwordText.collectAsState()
    val confirmedPasswordTextState = viewModel.confirmedPasswordText.collectAsState()


    //LifeCycle
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(RegisterEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(RegisterEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    //Webpage content
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
                        classes(
                            "mdc-button",
                            "mdc-button--raised",
                            "mdc-top-app-bar__navigation-icon"
                        )
                        onClick { viewModel.onEvent(RegisterEvent.OnLoginScreen) }
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

    MainFlexContainer {
        // -- Register Form --
        H1(attrs = { classes(AppStylesheet.h1) }) { Text("Register") }
        Form(attrs = { //Probably possible with just a button OnClick instead of Form&Submit
            this.addEventListener("submit") {
                console.log("$firstNameTextState, $lastNameTextState, $emailTextState, $passwordTextState")
                viewModel.onEvent(RegisterEvent.OnRegister)
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
                        classes("mdc-text-field", "mdc-text-field--filled", AppStylesheet.width)
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
                        required()
                        value(firstNameTextState.value.firstName)
                        onInput {
                            viewModel.onEvent(RegisterEvent.EnteredFirstname(it.value))
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
                        classes("mdc-text-field", "mdc-text-field--filled", AppStylesheet.width)
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
                        required()
                        value(lastNameTextState.value.lastName)
                        onInput {
                            viewModel.onEvent(RegisterEvent.EnteredLastname(it.value))
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
                        classes("mdc-text-field", "mdc-text-field--filled", AppStylesheet.width)
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
                    EmailInput(value = emailTextState.value.email,
                        attrs = {
                            required()
                            classes("mdc-text-field__input")
                            onInput {
                                viewModel.onEvent(RegisterEvent.EnteredEmail(it.value))

                            }
                        })
                    Span(
                        attrs = {
                            classes("mdc-line-ripple")
                        }
                    ) { }
                }
                if (!emailTextState.value.emailValid) {
                    Div(attrs = {style { color(Color.red) }}) {
                        Text("Email is not valid")
                    }
                }
            }
            Div(
                attrs = {
                    classes(AppStylesheet.margin)
                }
            ) {
                Label(
                    attrs = {
                        classes("mdc-text-field", "mdc-text-field--filled", AppStylesheet.width)
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
                    PasswordInput(value = passwordTextState.value.password,
                        attrs = {
                            required()
                            classes("mdc-text-field__input")
                            onInput {
                                viewModel.onEvent(RegisterEvent.EnteredPassword(it.value))
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
                        classes("mdc-text-field", "mdc-text-field--filled", AppStylesheet.width)
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
                    ) { Text("Repeat Password") }
                    PasswordInput(value = confirmedPasswordTextState.value.confirmedPassword,
                        attrs = {
                            required()
                            classes("mdc-text-field__input")
                            onInput {
                                viewModel.onEvent(RegisterEvent.EnteredConfirmedPassword(it.value))
                            }
                        })
                    Span(
                        attrs = {
                            classes("mdc-line-ripple")
                        }
                    ) { }
                }
                if (!confirmedPasswordTextState.value.confirmedPasswordValid) {
                    Div(attrs = {style { color(Color.red) }}) {
                        Text("Passwords do not match")
                    }
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
        }
    }
}

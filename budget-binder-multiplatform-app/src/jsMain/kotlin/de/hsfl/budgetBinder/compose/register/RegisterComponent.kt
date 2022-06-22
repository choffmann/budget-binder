package de.hsfl.budgetBinder.compose.register

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.compose.FeedbackSnackbar
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet

import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.viewmodel.register.RegisterEvent
import de.hsfl.budgetBinder.presentation.viewmodel.register.RegisterViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
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
    val loadingState = remember { mutableStateOf(false) }
    var openSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> {
                    // TODO: Refactor this, it's working but ahh
                    loadingState.value = true
                }
                else -> loadingState.value = false
            }
        }
    }
    if (loadingState.value) {
        Text("Loading")
        //TODO: LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    //Body
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
        H1 { Text(" Register") }
        Form(attrs = { //Probably possible with just a button OnClick instead of Form&Submit
            this.addEventListener("submit") {
                if (passwordTextState != confirmedPasswordTextState) {
                    openSnackbar = true
                }
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
    if (openSnackbar) {
        FeedbackSnackbar("Passwords do not match") { openSnackbar = false }
    }
}

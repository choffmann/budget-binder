package de.hsfl.budgetBinder.compose.login

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.login.LoginEvent
import de.hsfl.budgetBinder.presentation.viewmodel.login.LoginViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun LoginComponent() {
    val scope = rememberCoroutineScope()
    val viewModel: LoginViewModel by di.instance()
    val emailTextState = viewModel.emailText.collectAsState(scope.coroutineContext)
    val passwordTextState = viewModel.passwordText.collectAsState(scope.coroutineContext)
    val loadingState = remember { mutableStateOf(false) }


    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                else -> loadingState.value = false
            }
        }
    }

    if (loadingState.value) {
        Text("Loading")
        //LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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
                        classes(
                            "mdc-button",
                            "mdc-button--raised",
                            "mdc-top-app-bar__navigation-icon"
                        )
                        onClick { viewModel.onEvent(LoginEvent.OnRegisterScreen) }
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
                    console.log("$emailTextState, $passwordTextState")
                    viewModel.onEvent(LoginEvent.OnLogin)
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
                    EmailInput(value = emailTextState.value.email,
                        attrs = {
                            classes("mdc-text-field__input")
                            onInput {
                                viewModel.onEvent(LoginEvent.EnteredEmail(it.value))

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
                            classes("mdc-text-field__input")
                            onInput {
                                viewModel.onEvent(LoginEvent.EnteredPassword(it.value))
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
}

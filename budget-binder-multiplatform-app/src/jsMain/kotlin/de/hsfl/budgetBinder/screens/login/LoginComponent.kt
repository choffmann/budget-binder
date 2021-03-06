package de.hsfl.budgetBinder.screens.login

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.auth.login.LoginEvent
import de.hsfl.budgetBinder.presentation.viewmodel.auth.login.LoginViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun LoginComponent() {
    val scope = rememberCoroutineScope()
    val viewModel: LoginViewModel by di.instance()
    val emailTextState = viewModel.emailText.collectAsState(scope.coroutineContext)
    val passwordTextState = viewModel.passwordText.collectAsState(scope.coroutineContext)


    //LifeCycle
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(LoginEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(
                LoginEvent.LifeCycle(LifecycleEvent.OnDispose)
            )
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
        H1(attrs = { classes(AppStylesheet.h1) }) { Text("Login") }
        Form(
            attrs = {
                this.addEventListener("submit") {
                    viewModel.onEvent(LoginEvent.OnServerUrlDialogConfirm)
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
                    ) { Text("Email") }
                    EmailInput(value = emailTextState.value.email,
                        attrs = {
                            required()
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

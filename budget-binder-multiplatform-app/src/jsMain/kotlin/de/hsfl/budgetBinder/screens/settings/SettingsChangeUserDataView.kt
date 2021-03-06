package de.hsfl.budgetBinder.screens.settings

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.NavBar
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.EditUserEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEditUserViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun SettingsChangeUserDataView() {
    val viewModel: SettingsEditUserViewModel by di.instance()
    val loadingState = remember { mutableStateOf(false) }
    val firstNameText = viewModel.firstNameText.collectAsState()
    val lastNameText = viewModel.lastNameText.collectAsState()
    val passwordText = viewModel.passwordText.collectAsState()
    val confirmedPasswordText = viewModel.confirmedPassword.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.HideSuccess -> loadingState.value = false
                else -> loadingState.value = false
            }
        }
    }
    NavBar {}
    MainFlexContainer {
        H1(attrs = { classes(AppStylesheet.h1) }) { Text("Change User Data") }
        Form(attrs = {
            this.addEventListener("submit") {
                viewModel.onEvent(EditUserEvent.OnUpdate)
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
                        required()
                        classes("mdc-text-field__input")
                        value(firstNameText.value.firstName)
                        onInput {
                            viewModel.onEvent(EditUserEvent.EnteredFirstName(it.value))
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
                        required()
                        classes("mdc-text-field__input")
                        value(lastNameText.value.lastName)
                        onInput {
                            viewModel.onEvent(EditUserEvent.EnteredLastName(it.value))
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
                    ) { Text("Password") }
                    PasswordInput(value = passwordText.value.password,
                        attrs = {
                            required()
                            classes("mdc-text-field__input")
                            onInput {
                                viewModel.onEvent(EditUserEvent.EnteredPassword(it.value))
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
                    PasswordInput(value = confirmedPasswordText.value.confirmedPassword,
                        attrs = {
                            required()
                            classes("mdc-text-field__input")
                            onInput {
                                viewModel.onEvent(EditUserEvent.EnteredConfirmedPassword(it.value))
                            }
                        })
                    Span(
                        attrs = {
                            classes("mdc-line-ripple")
                        }
                    ) { }
                }
                if (!confirmedPasswordText.value.confirmedPasswordIsValid) {
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

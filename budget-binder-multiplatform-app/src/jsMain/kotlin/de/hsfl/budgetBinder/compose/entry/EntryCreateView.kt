package de.hsfl.budgetBinder.compose.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.*
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.Path
import org.jetbrains.compose.web.svg.Svg


@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun EntryCreateView(
    categoryList: List<Category>,
    onCreateEntryButtonPressed: () -> Unit,
) {
    var switchState by remember { mutableStateOf(false) }
    var entryNameTextFieldState by remember { mutableStateOf("") }
    var entryAmountTextFieldState by remember { mutableStateOf("") }
    var entryRepeatState by remember { mutableStateOf("") }
    var entryCategoryIDTextFieldState by remember { mutableStateOf("") }
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
        H1(
            attrs = {
                style { margin(2.percent) }
            }
        ) { Text("Create new Entry") }
        Form(attrs = {
            this.addEventListener("submit") {
                console.log("$entryNameTextFieldState, $entryAmountTextFieldState, $entryRepeatState, $entryCategoryIDTextFieldState")
                onCreateEntryButtonPressed(
                    entryNameTextFieldState,
                    (if (!switchState) "-$entryAmountTextFieldState" else entryAmountTextFieldState).toFloat(),
                    entryRepeatState.toBoolean(),
                    entryCategoryIDTextFieldState.toInt()
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
                    ) { Text("Entry Name") }
                    Input(
                        type = InputType.Text
                    ) {
                        classes("mdc-text-field__input")
                        value(entryNameTextFieldState)
                        required(true)
                        onInput {
                            entryNameTextFieldState = it.value
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
                        classes("mdc-text-field", "mdc-text-field--outlined")
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
                            style { marginBottom(1.percent) }
                        }
                    ) { Text("Amount") }
                    Div {
                        Button(
                            attrs = {
                                if (!switchState) classes("mdc-switch", "mdc-switch--unselected")
                                else classes("mdc-switch", "mdc-switch--selected")
                                id("basic-switch")
                                attr("role", "switch")
                                attr("aria-checked", "false")
                                type(ButtonType.Button)
                                onClick { switchState = !switchState }
                            }
                        ) {
                            Div(attrs = { classes("mdc-switch__track") }) { }
                            Div(attrs = { classes("mdc-switch__handle-track") }) {
                                Div(attrs = { classes("mdc-switch__handle") }) {
                                    Div(attrs = { classes("mdc-switch__shadow") }) {
                                        Div(attrs = { classes("mdc-elevation-overlay") }) { }
                                    }
                                    Div(attrs = { classes("mdc-switch__ripple") }) { }
                                    Div(attrs = { classes("mdc-switch__icons") }) {
                                        Svg(
                                            attrs = { classes("mdc-switch__icon", "mdc-switch__icon") },
                                            viewBox = "0 0 24 24"
                                        ) {
                                            Path("M19.69,5.23L8.96,15.96l-4.23-4.23L2.96,13.5l6,6L21.46,7L19.69,5.23z")
                                        }
                                        Svg(
                                            attrs = { classes("mdc-switch__icon", "mdc-switch__icon") },
                                            viewBox = "0 0 24 24"
                                        ) {
                                            Path("M20 13H4v-2h16v2z")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Div(attrs = {
                        classes("mdc-typography--headline6", AppStylesheet.text)
                    }) {
                        Text(if (switchState) "+" else "-")
                    }
                    Input(
                        type = InputType.Number
                    ) {
                        attr("step", "0.01")
                        classes("mdc-text-field__input")
                        onInput {
                            entryAmountTextFieldState = it.value.toString()
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
                    classes(AppStylesheet.margin, AppStylesheet.flexContainer)
                }
            ) {
                Div(attrs = {style { flex(50.percent) }}) {
                    Div(attrs = { classes("mdc-form-field") }) {
                        Div(attrs = { classes("mdc-checkbox") }) {
                            Input(type = InputType.Checkbox)
                            {
                                classes("mdc-checkbox__native-control")
                                id("checkbox-1")
                                onInput {
                                    entryRepeatState = it.value.toString()
                                }
                            }
                            Div(attrs = { classes("mdc-checkbox__background") }) {
                                Svg(attrs = { classes("mdc-checkbox__checkmark") }, viewBox = "0 0 24 24") {
                                    Path("M1.73,12.91 8.1,19.28 22.79,4.59", attrs = { classes("mdc-checkbox__checkmark") })
                                }
                                Div(attrs = { classes("mdc-checkbox__mixedmark") }) { }
                            }
                            Div(attrs = { classes("mdc-checkbox__ripple") }) { }
                        }
                        Label(forId = "checkbox-1") { Text("repeat") }
                    }
                }
                Div(attrs = {style { flex(50.percent) }}) {
                    ChooseCategoryMenu(categoryList) { id -> entryCategoryIDTextFieldState = id.toString() }
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
            Div {
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

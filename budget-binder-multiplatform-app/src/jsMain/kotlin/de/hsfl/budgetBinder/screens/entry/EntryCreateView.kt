package de.hsfl.budgetBinder.screens.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.*
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryViewModel
import di
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.Path
import org.jetbrains.compose.web.svg.Svg
import org.kodein.di.instance


@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun EntryCreateView(
    onCreateButton: () -> Unit,
) {
    val viewModel: EntryViewModel by di.instance()
    //Input
    val entryNameTextField by viewModel.nameText.collectAsState()
    val entryAmountTextField by viewModel.amountText.collectAsState()
    val entryCategoryIDTextField by viewModel.categoryIDState.collectAsState()
    val amountSign by viewModel.amountSignState.collectAsState()
    //Data
    val categoryList by viewModel.categoryListState.collectAsState()

    H1(
        attrs = {
            style { margin(2.percent) }
        }
    ) { Text("Create new Entry") }
    Form(attrs = {
        this.addEventListener("submit") {
            onCreateButton()
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
                    value(entryNameTextField)
                    required(true)
                    onInput {
                        viewModel.onEvent(EntryEvent.EnteredName(it.value))
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
                            if (!amountSign) classes("mdc-switch", "mdc-switch--unselected")
                            else classes("mdc-switch", "mdc-switch--selected")
                            id("basic-switch")
                            attr("role", "switch")
                            attr("aria-checked", "false")
                            type(ButtonType.Button)
                            onClick { viewModel.onEvent(EntryEvent.EnteredAmountSign) }
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
                    Text(if (amountSign) "+" else "-")
                }
                Input(
                    type = InputType.Number
                ) {
                    attr("step", "0.01")
                    value(entryAmountTextField)
                    classes("mdc-text-field__input")
                    onInput {
                        viewModel.onEvent(EntryEvent.EnteredAmount(it.value!!.toFloat()))
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
            Div(attrs = { style { flex(50.percent) } }) {
                Div(attrs = { classes("mdc-form-field") }) {
                    Div(attrs = { classes("mdc-checkbox") }) {
                        Input(type = InputType.Checkbox)
                        {
                            classes("mdc-checkbox__native-control")
                            id("checkbox-1")
                            onInput {
                                viewModel.onEvent(EntryEvent.EnteredRepeat)
                            }
                        }
                        Div(attrs = { classes("mdc-checkbox__background") }) {
                            Svg(
                                attrs = { classes("mdc-checkbox__checkmark") },
                                viewBox = "0 0 24 24"
                            ) {
                                Path(
                                    "M1.73,12.91 8.1,19.28 22.79,4.59",
                                    attrs = { classes("mdc-checkbox__checkmark") })
                            }
                            Div(attrs = { classes("mdc-checkbox__mixedmark") }) { }
                        }
                        Div(attrs = { classes("mdc-checkbox__ripple") }) { }
                    }
                    Label(forId = "checkbox-1") { Text("repeat") }
                }
            }
            Div(attrs = { style { flex(50.percent) } }) {
                ChooseCategoryMenu(categoryList, entryCategoryIDTextField) { id ->
                    viewModel.onEvent(EntryEvent.EnteredCategoryID(id))
                }
            }
        }
        Div(
            attrs = {
                classes(AppStylesheet.margin)
            }
        ) {
            Button(
                attrs = {
                    classes("mdc-button", "mdc-button--raised")
                    type(ButtonType.Button)
                    onClick { viewModel.onEvent(EntryEvent.OnCancel) }
                }
            ) {
                Span(attrs = { classes("mdc-button__label") }
                ) { Text("Cancel") }
            }
            SubmitInput(
                attrs = {
                    classes("mdc-button", "mdc-button--raised")
                    value("Submit")
                })
        }
    }
}


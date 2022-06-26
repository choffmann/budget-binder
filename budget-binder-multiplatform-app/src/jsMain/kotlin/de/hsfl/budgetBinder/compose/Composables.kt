package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*


/*Main Container for every mayor layout*/
@Composable
fun MainFlexContainer(content: @Composable () -> Unit) {
    Div(
        attrs = {
            classes("mdc-top-app-bar--fixed-adjust", AppStylesheet.flexContainer)
        }
    ) {
        Div(attrs = { classes(AppStylesheet.pufferFlexContainer) })
        Div(attrs = { classes(AppStylesheet.contentFlexContainer) })
        {
            Div(attrs = {
                classes("mdc-card", AppStylesheet.card)
            }
            ) {
                content()
            }
        }
        Div(attrs = { classes(AppStylesheet.pufferFlexContainer) })
    }
}

///* Gives a material icon based on the icon name*///
@Composable
fun Icon(icon_name: String) {
    Span(
        attrs = {
            classes("material-icons")
            style {
                width(24.px)
                height(24.px)
            }
        }
    ) { Text(icon_name) }
}


// Snackbar that shows msg
@Composable
fun FeedbackSnackbar(msg: String, hidden: Boolean = false, onDismiss: () -> Unit) {
    Aside(
        attrs = {
            when (hidden) {
                false -> classes("mdc-snackbar", "mdc-snackbar--open")
                true -> classes("mdc-snackbar")
            }
            onClick {
                onDismiss()
            }
        }) {
        Div(attrs = {
            classes("mdc-snackbar__surface")
            attr(attr = "role", value = "status")
            attr(attr = "aria-relevant", value = "additions")

        }) {
            Div(attrs = {
                classes("mdc-snackbar__label")
                attr(attr = "aria-atomic", value = "false")
            }) {
                Text(msg)
            }
            Div(attrs = {
                classes("mdc-snackbar__actions")
                attr(attr = "aria-atomic", value = "true")
            }) {
                Button(attrs = {
                    classes("mdc-button", "mdc-snackbar__action")
                    onClick { onDismiss() }
                }) {
                    Div(attrs = {
                        classes("mdc-button__ripple")
                    })
                    Span(attrs = { classes("mdc-button__label") })
                    { Text("Dismiss") }
                }
            }
        }
    }
}

@Composable
fun DeleteDialog(
    hidden: Boolean,
    buttonAction: () -> Unit,
    resetDialog: () -> Unit,
    content: @Composable () -> Unit
) {
    var hiddenValue by remember { mutableStateOf(hidden) }
    Div(
        attrs = {
            when (hiddenValue) {
                false -> classes("mdc-dialog", "mdc-dialog--open")
                true -> classes("mdc-dialog")
            }
        }
    ) {
        Div(
            attrs = {
                classes("mdc-dialog__container")
            }
        ) {
            Div(
                attrs = {
                    classes("mdc-dialog__surface")
                    attr("role", "alertdialog")
                    attr("aria-modal", "true")
                    attr("aria-labelledby", "my-dialog-title")
                    attr("aria-describedby", "my-dialog-content")
                }
            ) {
                Div(
                    attrs = {
                        classes("mdc-dialog__content")
                        id("my-dialog-content")
                    }
                ) {
                    content() //Text in Dialog
                }
                Div(
                    attrs = {
                        classes("mdc-dialog__actions")
                    }
                ) {
                    Button(
                        attrs = {
                            classes("mdc-button", "mdc-dialog__button")
                            attr("data-mdc-dialog-action", "cancel")
                            onClick {
                                hiddenValue = true
                                resetDialog()
                            }
                        }
                    ) {
                        Div(
                            attrs = {
                                classes("mdc-button__ripple")
                            }
                        ) {

                        }
                        Span(
                            attrs = {
                                classes("mdc-button__label")
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                    Button(
                        attrs = {
                            classes("mdc-button", "mdc-dialog__button")
                            attr("data-mdc-dialog-action", "accept")
                            onClick {
                                buttonAction()
                                hiddenValue = true
                                resetDialog()
                            }
                        }
                    ) {
                        Div(
                            attrs = {
                                classes("mdc-button__ripple")
                            }
                        ) {

                        }
                        Span(
                            attrs = {
                                classes("mdc-button__label")
                            }
                        ) {
                            Text("OK")
                        }
                    }
                }
            }
        }
        Div(
            attrs = {
                classes("mdc-dialog__scrim")
            }
        ) {

        }
    }
}





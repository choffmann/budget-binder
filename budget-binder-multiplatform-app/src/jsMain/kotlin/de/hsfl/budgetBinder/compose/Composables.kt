package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import de.hsfl.budgetBinder.common.StateManager.screenState
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.Screen
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
            content()
        }
        Div(attrs = { classes(AppStylesheet.pufferFlexContainer) })
    }
}

@Composable
fun topBarMain(logoButton: @Composable () -> Unit, navButtons: @Composable () -> Unit) {
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
                logoButton()
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
                navButtons()
            }
        }
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
fun FeedbackSnackbar(msg: String) {
    Aside(attrs = { classes("mdc-snackbar") }) {
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
                Button(attrs = { classes("mdc-button", "mdc-snackbar__action") }) {
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



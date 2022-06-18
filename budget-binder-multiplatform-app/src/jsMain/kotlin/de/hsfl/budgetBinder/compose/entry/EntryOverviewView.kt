package de.hsfl.budgetBinder.compose.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.CategoryList
import de.hsfl.budgetBinder.compose.DeleteDialog
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.topBarMain
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.Circle
import org.jetbrains.compose.web.svg.Svg


@Composable
fun EntryOverviewView(
    state: State<Any>,
    onEditButton: () -> Unit,
    onDeleteButton: (id: Int) -> Unit,
    onChangeToDashboard: () -> Unit,
    onChangeToCategory: () -> Unit,
    onChangeToSettings: () -> Unit
) {
    var entry by remember { mutableStateOf<Entry>(Entry(0, "", 0f, false, null)) }
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
        Div(attrs = {
            classes("mdc-card", AppStylesheet.card)
        }
        ) {
            H1(
                attrs = {
                    style { margin(2.percent) }
                }
            ) { Text(" Entry") }

            EntryOverview(entry, onEditButton, onDeleteButton)
        }
        when (viewState) {
            is UiState.Success<*> -> {
                when (val element = (viewState as UiState.Success<*>).element) {
                    is Entry -> entry = element
                    else -> {}
                }

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

@Composable
fun EntryOverview(
    entry: Entry,
    onEditButton: () -> Unit,
    onDeleteButton: (Int) -> Unit
) {
    var deleteDialog by remember { mutableStateOf(false) }
    Div(
        attrs = {
            classes(AppStylesheet.categoryListElement, AppStylesheet.flexContainer)
        }
    ) {

        Div(
            attrs = {
                classes(AppStylesheet.categoryListElementText)
            }
        ) {
            Div {
                Div(attrs = {
                    classes("mdc-typography--headline4", AppStylesheet.text)
                }) { Text(entry.name) }
                Div(attrs = {
                    classes("mdc-typography--headline6", AppStylesheet.text)
                }) { Text("Amount: ${entry.amount}€") }
            }
        }
    }
    Div(
        attrs = {
            classes(AppStylesheet.flexContainer)
        }
    ) {
        Button(attrs = {
            classes("mdc-button", "mdc-button--raised", AppStylesheet.marginRight)
            onClick { onEditButton() }
            style {
                flex(50.percent)
                margin(1.5.percent)
            }
        }) {
            Text("Edit Entry")
        }
        Button(attrs = {
            classes("mdc-button", "mdc-button--raised")
            onClick { deleteDialog = true }
            style {
                flex(50.percent)
                margin(1.5.percent)
                backgroundColor(Color("#b00020"))
            }
        }) {
            Text("Delete Entry")
        }
    }
    if (deleteDialog) {
        DeleteDialog(
            false,
            { onDeleteButton(entry.id) },
            { deleteDialog = false }) { Text("Delete Entry?") }
    }
}



package de.hsfl.budgetBinder.compose.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.topBarMain
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.*


@Composable
fun CategorySummaryView(
    state: State<Any>,
    onBackButton: () -> Unit,
    onEditButton: () -> Unit,
    onCategoryCreateButton: () -> Unit,
    onChangeToDashboard: () -> Unit,
    onChangeToCategory: () -> Unit,
    onChangeToSettings: () -> Unit
) {
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
            ) { Text(" Category Summary") }
            Button(attrs = {
                classes("mdc-button", "mdc-button--raised")
                onClick { onCategoryCreateButton() }
                style { margin(2.percent) }
            }) {
                Text("Create Category")
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
                Div(attrs = {
                    classes("mdc-card", AppStylesheet.card)
                }
                ) {
                    Text("Kat 1")
                    Button(attrs = {
                        classes("mdc-button", "mdc-button--raised")
                        onClick { onEditButton() }
                    }) {
                        Text("Edit Category (Needs to be set for each category)")
                    }
                }
                Div(attrs = {
                    classes("mdc-card", AppStylesheet.card)
                }
                ) {
                    Text("Kat 2")
                    Button(attrs = {
                        classes("mdc-button", "mdc-button--raised")
                        onClick { onEditButton() }
                    }) {
                        Text("Edit Category (Needs to be set for each category)")
                    }
                }
            }
        }
    }
}

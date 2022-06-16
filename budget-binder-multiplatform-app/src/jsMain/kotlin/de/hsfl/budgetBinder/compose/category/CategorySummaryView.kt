package de.hsfl.budgetBinder.compose.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.StateManager.screenState
import de.hsfl.budgetBinder.compose.CategoryList
import de.hsfl.budgetBinder.compose.DeleteDialog
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.topBarMain
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.css.flex
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.*


@Composable
fun CategorySummaryView(
    state: State<Any>,
    onCategoryCreateButton: () -> Unit,
    onChangeToDashboard: () -> Unit,
    onChangeToCategory: () -> Unit,
    onChangeToSettings: () -> Unit
) {
    var deleteDialog by remember { mutableStateOf(false) }
    var categoryList by remember { mutableStateOf<List<Category>>(emptyList()) }
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
            CategoryList(categoryList, {deleteDialog = true})
            Div {
                when (viewState) {
                    is UiState.Success<*> -> {
                        when (val element = (viewState as UiState.Success<*>).element) {
                            is List<*> -> {
                                element.filterIsInstance<Category>()
                                    .let {
                                        if (it.size == element.size) {
                                            categoryList = it
                                        }
                                    }
                            }
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
        if (deleteDialog) {
            DeleteDialog(false, {}, {deleteDialog = false}) { Text("Delete User?") }
        }
    }
}

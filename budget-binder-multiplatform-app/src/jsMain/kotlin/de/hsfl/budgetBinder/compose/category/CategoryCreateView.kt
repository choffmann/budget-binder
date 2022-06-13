package de.hsfl.budgetBinder.compose.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.CategoryImagesToImageList
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.topBarMain
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.attributes.required
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*


@Composable
fun CategoryCreateView(
    state: State<Any>,
    onCreateCategoryButtonPressed: (name: String, color: String, image: Category.Image, budget: Float) -> Unit,
    onChangeToDashboard: () -> Unit,
    onChangeToSettings: () -> Unit,
    onChangeToCategory: () -> Unit,
) {
    var categoryNameTextFieldState by remember { mutableStateOf("") }
    var categoryColorTextFieldState by remember { mutableStateOf("") }
    var categoryImageState by remember { mutableStateOf(Category.Image.DEFAULT) }
    var categoryBudgetTextFieldState  by remember { mutableStateOf("") }
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
            H1 { Text("Create a new Category") }
            Form(attrs = {
                this.addEventListener("submit") {
                    console.log("$categoryNameTextFieldState, $categoryColorTextFieldState, $categoryImageState, $categoryBudgetTextFieldState")
                    onCreateCategoryButtonPressed(
                        categoryNameTextFieldState,
                        categoryColorTextFieldState,
                        categoryImageState,
                        categoryBudgetTextFieldState.toFloat()
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
                        ) { Text("Category Name") }
                        Input(
                            type = InputType.Text
                        ) {
                            classes("mdc-text-field__input")
                            value(categoryNameTextFieldState)
                            required(true)
                            onInput {
                                categoryNameTextFieldState = it.value
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
                            style { width(50.percent) }
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
                        ) { Text("Color") }
                        Input(
                            type = InputType.Color
                        ) {
                            classes("mdc-text-field__input")
                            value(categoryColorTextFieldState)
                            onInput {
                                categoryColorTextFieldState = it.value
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
                            style { width(100.percent) }
                        }
                    ) {
                        Span(
                            attrs = {
                                classes("mdc-floating-label", "mdc-floating-label--float-above")
                                style { marginBottom(1.percent); marginLeft(2.percent)}
                            }
                        ) { Text("Image") }
                        CategoryImagesToImageList(onClick = {categoryImageState = it})
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
                        ) { Text("Budget") }
                        Input(
                            type = InputType.Number
                        ) {
                            classes("mdc-text-field__input")
                            value(categoryBudgetTextFieldState)
                            required(true)
                            min("1")
                            onInput {
                                categoryBudgetTextFieldState = it.value.toString()
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
}

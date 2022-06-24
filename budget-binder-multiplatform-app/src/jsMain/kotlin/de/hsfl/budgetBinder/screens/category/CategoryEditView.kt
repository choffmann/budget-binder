package de.hsfl.budgetBinder.screens.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.CategoryImagesToImageList
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditViewModel
import di
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun CategoryEditView() {
    val viewModel: CategoryEditViewModel by di.instance()
    val categoryNameState by viewModel.categoryNameState.collectAsState()
    val categoryColorState by viewModel.categoryColorState.collectAsState()
    val categoryImageState by viewModel.categoryImageState.collectAsState()
    val categoryBudgetState by viewModel.categoryBudgetState.collectAsState()

    //Life Cycle
    LaunchedEffect(Unit) {
        viewModel.onEvent(CategoryEditEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(CategoryEditEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    //Webpage Content
    H1(attrs = { classes(AppStylesheet.h1) }) { Text("Edit Category") }

    Form(attrs = {
        this.addEventListener("submit") {
            viewModel.onEvent(CategoryEditEvent.OnSave)
            it.preventDefault()
        }
    }
    ) {
        //Category Name Input
        Div(
            attrs = {
                classes(AppStylesheet.margin)
            }
        ) {
            Label(
                attrs = {
                    classes("mdc-text-field", "mdc-text-field--filled")
                    classes(AppStylesheet.width)
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
                    value(categoryNameState)
                    required(true)
                    onInput {
                        viewModel.onEvent(CategoryEditEvent.EnteredCategoryName(it.value))
                    }
                }
                Span(
                    attrs = {
                        classes("mdc-line-ripple")
                    }
                ) { }
            }
        }
        //Category Color Input
        Div(
            attrs = {
                classes(AppStylesheet.margin)
            }
        ) {
            Label(
                attrs = {
                    classes("mdc-text-field", "mdc-text-field--outlined")
                    classes(AppStylesheet.width)
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
                    value("#$categoryColorState")
                    onInput {
                        viewModel.onEvent(CategoryEditEvent.EnteredCategoryColor(it.value.drop(1)))
                    }
                }
                Span(
                    attrs = {
                        classes("mdc-line-ripple")
                    }
                ) { }
            }
        }
        //Category Image Input
        Div(
            attrs = {
                classes(AppStylesheet.margin)
            }
        ) {
            Label(
                attrs = {
                    classes(AppStylesheet.width)
                }
            ) {
                Span(
                    attrs = {
                        classes("mdc-floating-label", "mdc-floating-label--float-above")
                        style { marginBottom(1.percent); marginLeft(2.percent) }
                    }
                ) { Text("Image") }
                CategoryImagesToImageList(
                    categoryImageState,
                    onClick = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryImage(it)) })
            }
        }
        //Category Budget Input
        Div(
            attrs = {
                classes(AppStylesheet.margin)
            }
        ) {
            Label(
                attrs = {
                    classes("mdc-text-field", "mdc-text-field--filled")
                    classes(AppStylesheet.width)
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
                    value(categoryBudgetState)
                    attr("step", "0.01")
                    required(true)
                    min("1")
                    onInput {
                        viewModel.onEvent(CategoryEditEvent.EnteredCategoryBudget(it.value as Float))
                    }
                }
                Span(
                    attrs = {
                        classes("mdc-line-ripple")
                    }
                ) { }
            }
        }

        //Submit button
        Div(
            attrs = {
                classes(AppStylesheet.margin)
            }
        ) {
            Button(
                attrs = {
                    classes("mdc-button", "mdc-button--raised", AppStylesheet.marginRight)
                    type(ButtonType.Button)
                    onClick { viewModel.onEvent(CategoryEditEvent.OnCancel) }
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

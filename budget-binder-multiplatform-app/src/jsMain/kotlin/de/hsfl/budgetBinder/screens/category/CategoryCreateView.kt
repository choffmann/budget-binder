package de.hsfl.budgetBinder.screens.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.CategoryImagesToImageList
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.create.CategoryCreateEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.create.CategoryCreateViewModel
import di
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun CategoryCreateView() {
    val viewModel: CategoryCreateViewModel by di.instance()
    val categoryNameState by viewModel.categoryNameState.collectAsState()
    val categoryColorState by viewModel.categoryColorState.collectAsState()
    val categoryImageState by viewModel.categoryImageState.collectAsState()
    val categoryBudgetState by viewModel.categoryBudgetState.collectAsState()

    //Life Cycle
    LaunchedEffect(Unit) {
        viewModel.onEvent(CategoryCreateEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(CategoryCreateEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    //Webpage Content
    H1(
        attrs = {
            style { margin(2.percent) }
        }
    ) { Text("Create a new Category") }
    Form(attrs = {
        this.addEventListener("submit") {
            viewModel.onEvent(CategoryCreateEvent.OnSave)
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
                    value(categoryNameState)
                    required(true)
                    onInput {
                        viewModel.onEvent(CategoryCreateEvent.EnteredCategoryName(it.value))
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
                ) { Text("Color") }
                Input(
                    type = InputType.Color
                ) {
                    classes("mdc-text-field__input")
                    value("#$categoryColorState")
                    onInput {
                        viewModel.onEvent(CategoryCreateEvent.EnteredCategoryColor(it.value.drop(1)))
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
                    style { width(100.percent) }
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
                    onClick = { viewModel.onEvent(CategoryCreateEvent.EnteredCategoryImage(it)) })
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
                    value(categoryBudgetState)
                    required(true)
                    min("1")
                    onInput {
                        viewModel.onEvent(CategoryCreateEvent.EnteredCategoryBudget(it.value as Float))
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
                    onClick { viewModel.onEvent(CategoryCreateEvent.OnCancel) }
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

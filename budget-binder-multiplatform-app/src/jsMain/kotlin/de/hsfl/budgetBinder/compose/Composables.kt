package de.hsfl.budgetBinder.compose

import App
import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.theme.AppStylesheet.attr
import de.hsfl.budgetBinder.compose.theme.AppStylesheet.style
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import kotlinx.serialization.json.JsonNull.content
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.Circle
import org.jetbrains.compose.web.svg.Rect
import org.jetbrains.compose.web.svg.Svg


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

@Composable
fun CategoryImagesToImageList(onClick: (Category.Image) -> Unit) {
    var highlightImage by remember { mutableStateOf(Category.Image.DEFAULT) }
    Div(
        attrs = {
            classes("mdc-card", AppStylesheet.card)
        }
    ) {
        Ul(
            attrs = {
                classes("mdc-image-list", "my-image-list")
            }
        ) {
            for (image in Category.Image.values()) {
                Li(
                    attrs = {
                        classes("mdc-image-list__item")
                    }
                ) {
                    Div(
                        attrs = {
                            if (highlightImage == image)
                                classes(
                                    "mdc-image-list__image-aspect-container",
                                    "mdc-icon-button",
                                    "mdc-button--raised"
                                )
                            else classes("mdc-image-list__image-aspect-container", "mdc-icon-button")
                            onClick { onClick(image); highlightImage = image }
                        }
                    ) {
                        CategoryImageToIcon(image)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun CategoryList(
    categoryList: List<Category>,
    onOpenDeleteDialog: () -> Unit,
) {
    Div {
        for (category in categoryList)
            Div(attrs = {
                classes("mdc-card", AppStylesheet.card)
            }
            ) {
                Div(
                    attrs = {
                        classes(AppStylesheet.categoryListElement, AppStylesheet.flexContainer)
                    }
                ) {
                    Div(
                        attrs = {
                            classes(AppStylesheet.imageFlexContainer)
                        }
                    ) {
                        CategoryImageToIcon(category.image)
                    }
                    Div(
                        attrs = {
                            classes(AppStylesheet.categoryListElementText)
                        }
                    ) {
                        Div {
                            Div(attrs = {
                                classes("mdc-typography--headline4", AppStylesheet.text)
                            }) { Text(category.name) }
                            Div(attrs = {
                                classes("mdc-typography--headline6", AppStylesheet.text)
                            }) { Text("Budget: ${category.budget}€") }
                        }
                    }
                    Div(attrs = {
                            classes(AppStylesheet.imageFlexContainer)
                        }
                    ) {
                        Svg(viewBox = "0 0 1 1") {//For aspect ratio - tries to fill out wherever it is in
                            Circle(cx = 0.5, cy = 0.5, r=0.5, {
                                attr("fill", "#${category.color}")
                            })
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
                        onClick { }
                        style {
                            flex(50.percent)
                            margin(1.5.percent)
                        }
                    }) {
                        Text("Edit Category")
                    }
                    Button(attrs = {
                        classes("mdc-button", "mdc-button--raised")
                        onClick { onOpenDeleteDialog() }
                        style {
                            flex(50.percent)
                            margin(1.5.percent)
                            backgroundColor(Color("#b00020"))
                        }
                    }) {
                        Text("Delete Category")
                    }
                }
            }
    }
}

@Composable
fun DeleteDialog(hidden: Boolean, buttonAction: () -> Unit, resetDialog: () -> Unit, content: @Composable () -> Unit) {
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

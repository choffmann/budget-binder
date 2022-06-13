package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
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

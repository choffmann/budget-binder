package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.Circle
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
            Div(attrs = {
                classes("mdc-card", AppStylesheet.card)
            }
            ) {
                content()
            }
        }
        Div (attrs = { classes(AppStylesheet.pufferFlexContainer)})
    }
}

@Deprecated("Use new NavBar instead! just write NavBar{} over your MainFlexContainer!")
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
fun CategoryImagesToImageList(
    inputImage: Category.Image,
    onClick: (Category.Image) -> Unit
) {
    Div(
        attrs = {
            classes("mdc-card", AppStylesheet.card)
        }
    ) {
        Ul(
            attrs = {
                classes("mdc-image-list", AppStylesheet.categoryImageList)
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
                            if (inputImage == image)
                                classes(
                                    "mdc-image-list__image-aspect-container",
                                    "mdc-icon-button",
                                    "mdc-button--raised"
                                )
                            else classes(
                                "mdc-image-list__image-aspect-container",
                                "mdc-icon-button"
                            )
                            onClick { onClick(image) }
                        }
                    ) {
                        CategoryImageToIcon(image)
                    }
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

@Composable
fun ChooseCategoryMenu(
    categoryList: List<Category>,
    selectedCategory: Int?,
    getCategoryId: (Int?) -> Unit
) {
    console.log(categoryList)
    var choseCat = categoryList[0]

    for (category in categoryList) {
        if (category.id == selectedCategory) {
            choseCat = category
            break
        }
    }

    var chosenCategory by remember {
        mutableStateOf(choseCat)
    }
    console.log(chosenCategory)


    var showList by remember { mutableStateOf(false) }

    Button(attrs = {
        classes("mdc-button", "mdc-dialog__button")
        onClick { showList = !showList }
        type(ButtonType.Button)
    }) {
        Div(attrs = {
            when (showList) {
                true -> classes("mdc-menu", "mdc-menu-surface", "mdc-menu-surface--open")
                false -> classes("mdc-menu", "mdc-menu-surface")
            }
        }) {
            Ul(attrs = {
                classes("mdc-list")
                attr("role", "menu")
                attr("aria-hidden", "true")
                attr("aria-orientation", "vertical")
                attr("tabindex", "-1")
            }) {
                for (category in categoryList) {
                    Li(attrs = {
                        classes("mdc-list-item")
                        attr("role", "menuitem")
                        onClick { chosenCategory = category; getCategoryId(category.id) }
                    }) {
                        Span(attrs = { classes("mdc-list-item__ripple") }) { }
                        Span(attrs = { classes(AppStylesheet.moneyText)}) { Text(category.name) }
                    }
                }
            }
        }
        Text(chosenCategory.name)
    }
}



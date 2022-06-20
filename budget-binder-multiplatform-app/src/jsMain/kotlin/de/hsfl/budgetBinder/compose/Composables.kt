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
        Div(attrs = { classes(AppStylesheet.pufferFlexContainer)})
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
fun FeedbackSnackbar(msg: String, hidden: Boolean = false) {
    var hiddenValue by remember { mutableStateOf(hidden) }
    Aside(
        attrs = {
            when (hiddenValue) {
                false -> classes("mdc-snackbar", "mdc-snackbar--open")
                true -> classes("mdc-snackbar", "maria")
            }
            onClick {
                hiddenValue = true
                console.log(this@Aside)
                console.log("ldsadsad")

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
    inputImage: MutableState<Category.Image>,
    onClick: (Category.Image) -> Unit
) {
    val highlightImage by remember { mutableStateOf(inputImage) }
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
                            if (highlightImage.value == image)
                                classes(
                                    "mdc-image-list__image-aspect-container",
                                    "mdc-icon-button",
                                    "mdc-button--raised"
                                )
                            else classes("mdc-image-list__image-aspect-container", "mdc-icon-button")
                            onClick { onClick(image); highlightImage.value = image }
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
    onEditButton: (Int) -> Unit,
    onDeleteButton: (Int) -> Unit
) {
    var deleteDialog by remember { mutableStateOf(false) }
    var id by remember { mutableStateOf(0) }

    Div {
        if (deleteDialog) {
            DeleteDialog(
                false,
                { onDeleteButton(id) },
                { deleteDialog = false }) { Text("Delete Category?") }
        }
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
                            Circle(cx = 0.5, cy = 0.5, r = 0.5, {
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
                        onClick { onEditButton(category.id) }
                        style {
                            flex(50.percent)
                            margin(1.5.percent)
                        }
                    }) {
                        Text("Edit Category")
                    }
                    Button(attrs = {
                        classes("mdc-button", "mdc-button--raised")
                        onClick { deleteDialog = !deleteDialog; id = category.id }
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

@Composable
fun ChooseCategoryMenu(
    categoryList: List<Category>,
    getCategoryId: (Int?) -> Unit
) {
    var chosenCategory by remember { mutableStateOf(categoryList[0]) }
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
                        Span(attrs = {  }) { Text(category.name) }
                    }
                }
            }
        }
        Text(chosenCategory.name)
    }
}


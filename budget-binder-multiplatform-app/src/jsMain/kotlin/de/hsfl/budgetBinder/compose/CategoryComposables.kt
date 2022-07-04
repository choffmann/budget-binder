package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.Circle
import org.jetbrains.compose.web.svg.Rect
import org.jetbrains.compose.web.svg.Svg

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun BudgetBar(
    focusedCategory: Category,
    totalSpendBudget: Float,
    totalBudget: Float,
) {
    //width and height are for aspect ratio - tries to fill out wherever its in, so its more like
    val width = 20
    val height = 2

    H1(attrs = { classes("mdc-typography--headline4", AppStylesheet.flexContainer) }) {
        CategoryImageToIcon(focusedCategory.image)
        Text("${focusedCategory.name} - Budget")
    }

    Div {
        if (totalSpendBudget <= totalBudget && totalBudget > 0) { //Normal not Spent Budget
            //Money Text
            MoneyTextDiv {
                Div(attrs = {
                    classes("mdc-typography--headline5")
                }) { Text(totalSpendBudget.toString() + "€") }
                Div(attrs = { classes("mdc-typography--headline5") }) { Text(totalBudget.toString() + "€") }
            }
            Svg(viewBox = "0 0 $width $height") {
                Rect(x = 0, y = 0, width = width, height = height, {
                    attr("fill", Color.lightgray.toString())
                })
                if (0 < totalSpendBudget) // If there is used budget, draw it
                    Rect(
                        x = 0,
                        y = 0,
                        width = totalSpendBudget / totalBudget * width,
                        height = height,
                        {
                            attr("fill", "#" + focusedCategory.color)
                        })
            }
        } else if (totalSpendBudget > totalBudget && totalBudget > 0) { //Over Budget
            MoneyTextDiv {
                Div(attrs = { classes("mdc-typography--headline5") }) { Text("Budget limit for " + focusedCategory.name + " reached! " + totalSpendBudget.toString() + "€ of " + totalBudget.toString() + "€ Budget spent") }
            }
            Svg(viewBox = "0 0 $width $height") {
                Rect(x = 0, y = 0, width = width, height = height, {
                    attr("fill", "#b00020")
                })
            }
        } else if (totalBudget <= 0f) { //No Category View or other unpredictable case (or no categories, overall screen)
            MoneyTextDiv {
                Div(attrs = { classes("mdc-typography--headline5") }) { Text(totalSpendBudget.toString() + "€ spent") }
            }
            Svg(viewBox = "0 0 $width $height") {
                Rect(x = 0, y = 0, width = width, height = height, {
                    attr("fill", "#" + focusedCategory.color)
                })
            }
        }
    }
}

@Composable
fun MoneyTextDiv(content: @Composable () -> Unit) {
    Div(attrs = {
        style {
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent("space-between"))
        }
    }) {
        content()
    }
}

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun CategoryList(
    categoryList: List<Category>,
    onClicked: (Int) -> Unit,
) {
    Div {
        for (category in categoryList)
            CategoryElement(category, onClicked = onClicked)
    }
}

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun CategoryElement(category: Category, onClicked: (Int) -> Unit) {
    Div(attrs = {
        classes("mdc-card", AppStylesheet.card)
        onClick { onClicked(category.id) }
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
            ColorCircle(category.color)
        }
    }
}

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun ColorCircle(color: String) {
    Div(attrs = {
        classes(AppStylesheet.colorFlexContainer)
    }
    ) {
        Svg(viewBox = "0 0 1 1") {//For aspect ratio - tries to fill out wherever it is in
            Circle(cx = 0.5, cy = 0.5, r = 0.5, {
                attr("fill", "#$color")
            })
        }
    }
}

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun CategoryDetailed(
    category: Category,
    onEditButton: () -> Unit,
    onDeleteButton: () -> Unit,
    onBackButton: () -> Unit
) {
    var deleteDialog by remember { mutableStateOf(false) }
    Div(attrs = {
        classes("mdc-card", AppStylesheet.card)
    }
    ) {
        if (deleteDialog) {
            DeleteDialog(
                false,
                { onDeleteButton() },
                { deleteDialog = false }) { Text("Delete Category?") }
        }
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
                classes("mdc-button", "mdc-button--raised", AppStylesheet.buttonOverview)
                onClick { onBackButton() }
            }
            ) {
                Span(attrs = { classes("mdc-button__label") }
                ) { Text("Go back") }
            }
            Button(attrs = {
                classes("mdc-button", "mdc-button--raised", AppStylesheet.buttonOverview)
                onClick { onEditButton() }
            }) {
                Text("Edit Category")
            }
            Button(attrs = {
                classes("mdc-button", "mdc-button--raised", AppStylesheet.buttonOverview)
                onClick { deleteDialog = !deleteDialog }
                style { backgroundColor(Color("#b00020")) }
            }) {
                Text("Delete Category")
            }
        }
    }
}

@Composable
fun ChooseCategoryMenu(
    categoryList: List<Category>,
    selectedCategory: Int?,
    getCategoryId: (Int?) -> Unit
) {
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
                        Span(attrs = { classes(AppStylesheet.moneyText) }) { Text(category.name) }
                    }
                }
            }
        }
        Text(chosenCategory.name)
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


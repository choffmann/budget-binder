package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
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
                    Rect(x = 0, y = 0, width = totalSpendBudget/ totalBudget * width, height = height, {
                        attr("fill", "#" + focusedCategory.color)
                    })
            }
        } else if (totalSpendBudget> totalBudget && totalBudget > 0) { //Over Budget
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

package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.css.paddingRight
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div


@Composable
fun DashboardData(
    focusedCategory: Category,
    totalSpendBudget: Float,
    totalBudget: Float,
    hasPrev: Boolean,
    hasNext: Boolean,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    SwipeContainer(hasPrev, hasNext, onPrevClicked, onNextClicked) {
        BudgetBar(focusedCategory, totalSpendBudget, totalBudget)
    }
}

@Composable
fun SwipeContainer(
    hasPrev: Boolean,
    hasNext: Boolean,
    onPrevClicked: () -> Unit,
    onNextClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    Div(
        attrs = {
            classes(AppStylesheet.flexContainer)
        }) {
        Div(attrs = {
            if (hasPrev) {
                classes(AppStylesheet.imageFlexContainer, "mdc-button")
                onClick { onPrevClicked() }
            } else {
                classes(AppStylesheet.imageFlexContainer)
                style {
                    paddingLeft(8.px)
                    paddingRight(8.px)
                }
            }
        }) {
            if (hasPrev) Icon("arrow_back_ios_new")
        }
        Div(attrs = { classes(AppStylesheet.budgetBarContainer) })
        {
            content()
        }
        Div(attrs = {
            if (hasNext) {
                classes(AppStylesheet.imageFlexContainer, "mdc-button")
                onClick { onNextClicked() }
            } else {
                classes(AppStylesheet.imageFlexContainer)
                style {
                    paddingLeft(8.px)
                    paddingRight(8.px)
                }
            }
        }) {
            if (hasNext) Icon("arrow_forward_ios_new")
        }
    }
}

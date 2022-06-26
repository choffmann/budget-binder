package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEntryState
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardState
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import kotlin.math.absoluteValue


/**
 * List of Entries, made to visualize Entries on Dashboard
 */
@Composable
fun EntryList(
    entryList: List<DashboardEntryState>,
    oldEntries: Map<String, DashboardState>,
    onItemClicked: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onEntryDelete: (Int) -> Unit

) {
    if (entryList.isEmpty()) {
        Div(attrs = {
            classes(
                "mdc-typography--headline5",
                AppStylesheet.text
            )
        }) { Text("This category has no current entries. You can create an new entry.") }
    } else {
        for (entry in entryList) {
            EntryListElement(entry, onItemClicked, onEntryDelete)
        }
    }

    DefaultText("Older entries...") { flex(100.percent) }
    for ((date, dashboardState) in oldEntries) {
        DefaultText(date) { flex(100.percent) }
        for (entry in dashboardState.entryList) {
            EntryListElement(entry, onItemClicked, onEntryDelete)
        }
    }
    Button(
        attrs = {
            classes("mdc-button", "mdc-button--raised", "mdc-top-app-bar__navigation-icon")
            onClick { onLoadMore() }
        }
    ) {
        Span(attrs = { classes("mdc-button__label") }
        ) { Text("Load more Entries") }
    }
}

/**
 * Visualisation of an Entry, made for EntryLists
 */
@Composable
fun EntryListElement(
    entry: DashboardEntryState,
    onItemClicked: (Int) -> Unit,
    onEntryDelete: (Int) -> Unit
) {
    Div(attrs = {
        classes("mdc-card", "mdc-card--outlined", AppStylesheet.entryListElement)
        onClick { onItemClicked(entry.entry.id) }
    }) {
        Div(attrs = {
            classes(
                AppStylesheet.entryListElementText,
                AppStylesheet.extraImagePadding,
                AppStylesheet.imageFlexContainer
            )
        }) {
            CategoryImageToIcon(entry.categoryImage)
        }
        ColorCircle(entry.categoryColor)
        Div(attrs = { classes(AppStylesheet.entryListElementText) }) {
            Div(attrs = {
                classes(
                    "mdc-typography--headline5",
                    AppStylesheet.leftText
                )
            }) { Text(entry.entry.name) }
        }
        Div(attrs = { classes(AppStylesheet.imageFlexContainer) }) {
            Div(attrs = {
                classes(
                    "mdc-typography--headline5",
                    AppStylesheet.moneyText
                )
            }) { Text(amountToString(entry.entry.amount)) }
        }
    }
}

/**
 * Converts a float number into a string with currency
 */
fun amountToString(amount: Float): String {
    //This whole thing just so it's "- 10 €" and not "-10 €"
    val x = if (amount < 0) "-" else ""
    return "$x ${amount.absoluteValue} €"
}


/**
 * Floating Action Button for Entry-Creation
 */
@Composable
fun CreateNewEntryButton(onEntryCreateButton: () -> Unit) {
    Div(attrs = {
        style {
            display(DisplayStyle.Flex)
            justifyContent(JustifyContent.FlexEnd)
        }
    }) {
        Button(attrs = {
            classes("mdc-fab", "mdc-fab--touch", AppStylesheet.newEntryButton)
            onClick { onEntryCreateButton() }
        }) {
            Div(attrs = { classes("mdc-fab__ripple") })
            Icon("add")
            Div(attrs = { classes("mdc-fab__touch") })
        }
    }
}

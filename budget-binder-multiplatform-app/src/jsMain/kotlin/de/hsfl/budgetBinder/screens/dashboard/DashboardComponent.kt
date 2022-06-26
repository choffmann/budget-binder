package de.hsfl.budgetBinder.screens.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.*
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEntryState
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardEvent
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardState
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance
import kotlin.math.absoluteValue

@Composable
fun DashboardComponent() {
    val viewModel: DashboardViewModel by di.instance()
    val entryList = viewModel.entryListState.collectAsState()
    val focusedCategory = viewModel.focusedCategoryState.collectAsState()
    val olderEntries = viewModel.oldEntriesMapState.collectAsState()
    val loadingState = remember { mutableStateOf(false) }


    //LifeCycle
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(DashboardEvent.LifeCycle(LifecycleEvent.OnLaunch))
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.HideSuccess -> loadingState.value = false
                else -> loadingState.value = false
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(DashboardEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    //Webpage content
    NavBar {}
    MainFlexContainer {
        Div {
            DashboardData(
                focusedCategory = focusedCategory.value.category,
                totalSpendBudget = focusedCategory.value.spendBudget,
                totalBudget = focusedCategory.value.category.budget,
                hasPrev = focusedCategory.value.hasPrev,
                hasNext = focusedCategory.value.hasNext,
                onPrevClicked = { viewModel.onEvent(DashboardEvent.OnPrevCategory) },
                onNextClicked = { viewModel.onEvent(DashboardEvent.OnNextCategory) }
            )
            EntryList(entryList = entryList.value,
                oldEntries = olderEntries.value,
                onItemClicked = { viewModel.onEvent(DashboardEvent.OnEntry(it)) },
                onLoadMore = { viewModel.onEvent(DashboardEvent.OnLoadMore) },
                onEntryDelete = { viewModel.onEvent(DashboardEvent.OnEntryDelete(it)) }
            )
        }
        CreateNewEntryButton { viewModel.onEvent(DashboardEvent.OnEntryCreate) }
    }
}

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
    SwipeContainer(
        hasPrev, hasNext, onPrevClicked, onNextClicked
    ) {
        BudgetBar(focusedCategory, totalSpendBudget, totalBudget)
    }
}


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

//TODO: Load Old Data and old Entries
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
    Text("Older entries...")
    for ((date, dashboardState) in oldEntries) {
        Text(date) //TODO-WEB: Sticky?
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

fun amountToString(amount: Float): String {
    //This whole thing just so it's "- 10 €" and not "-10 €"
    val x = if (amount < 0) "-" else ""
    return "$x ${amount.absoluteValue} €"
}




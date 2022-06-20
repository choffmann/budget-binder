package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.FeedbackSnackbar
import de.hsfl.budgetBinder.compose.Icon
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.category.BudgetBar
import de.hsfl.budgetBinder.compose.entry.EntryList
import de.hsfl.budgetBinder.compose.entry.entriesFromCategory
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.topBarMain
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.viewmodel.dashboard.DashboardViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance

@Composable
fun DashboardComponent(screenState: MutableState<Screen>) {
    val viewModel: DashboardViewModel by di.instance()
    val entryList = viewModel.entryListState.collectAsState()
    val focusedCategory = viewModel.focusedCategoryState.collectAsState()
    val totalSpendBudget = viewModel.spendBudgetOnCurrentCategory.collectAsState()
    val olderEntries = viewModel.oldEntriesMapState.collectAsState()
    val loadingState = remember { mutableStateOf(false) }
   // val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.HideSuccess -> loadingState.value = false
                else -> loadingState.value = false
            }
        }
    }
    //TODO: TOPBAR
    MainFlexContainer {
        Div { DashboardData(categoryList, entryList) { id -> onEntryOverviewButton(id) } }
        CreateNewEntryButton({ onEntryCreateButton(categoryList) })
        //Process new Category Data
        when (categoriesViewState) {
            is UiState.Success<*> -> {
                //Updates Data
                // https://stackoverflow.com/questions/36569421/kotlin-how-to-work-with-list-casts-unchecked-cast-kotlin-collections-listkot
                when (val element = (categoriesViewState as UiState.Success<*>).element) {
                    is List<*> -> {
                        element.filterIsInstance<Category>()
                            .let {
                                if (it.size == element.size) {
                                    categoryList = it
                                }
                            }
                    }
                }
            }
            is UiState.Error -> {
                FeedbackSnackbar((entriesViewState as UiState.Error).error)
            }
            is UiState.Loading -> {
                //CircularProgressIndicator()
            }
        }
        //Process new Entry Data
        when (entriesViewState) {
            is UiState.Success<*> -> {
                //Updates Data
                when (val element = (entriesViewState as UiState.Success<*>).element) {
                    is List<*> -> {
                        element.filterIsInstance<Entry>()
                            .let {
                                if (it.size == element.size) {
                                    entryList = it
                                }
                            }
                    }
                }
            }
            is UiState.Error -> {
                FeedbackSnackbar((entriesViewState as UiState.Error).error)
            }
            is UiState.Loading -> {
                //CircularProgressIndicator()
            }
        }
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
    SwipeContainer (
        hasPrev, hasNext, onPrevClicked, onNextClicked
    ){
        BudgetBar(categoryList[focusedCategory], filteredEntryList)
    }
}


@Composable
fun CreateNewEntryButton(onEntryCreateButton: () -> Unit) {
    Div (attrs = {style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.FlexEnd)
    }}) {
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
            if(hasPrev) {
                classes(AppStylesheet.imageFlexContainer, "mdc-button")
                onClick { onPrevClicked() }
            }
            else{
                classes(AppStylesheet.imageFlexContainer)
                style{
                    paddingLeft(8.px)
                    paddingRight(8.px)
                }
            }
        }){
            if(hasPrev) Icon("arrow_back_ios_new")
        }
        Div(attrs = { classes(AppStylesheet.budgetBarContainer) })
        {
            content()
        }
        Div(attrs = {
            if(hasNext) {
                classes(AppStylesheet.imageFlexContainer, "mdc-button")
                onClick { onNextClicked() }
            }
            else{
                classes(AppStylesheet.imageFlexContainer)
                style{
                    paddingLeft(8.px)
                    paddingRight(8.px)
                }
            }
        }) {
            if(hasNext) Icon("arrow_forward_ios_new")
        }
    }
}


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
import de.hsfl.budgetBinder.presentation.UiState
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*


@Composable
fun DashboardView(
    categoriesState: State<Any>,
    entriesState: State<Any>,
    onCategorySummaryButton: () -> Unit,
    onSettingsButton: () -> Unit,
    onEntryCreateButton: (categoryList: List<Category>) -> Unit,
    onEntryOverviewButton: (id:Int) -> Unit
) {
    val categoriesViewState by remember { categoriesState }
    val entriesViewState by remember { entriesState }
    var categoryList by remember { mutableStateOf<List<Category>>(emptyList()) }
    var entryList by remember { mutableStateOf<List<Entry>>(emptyList()) }
    topBarMain(
        logoButton = {
            Img(
                src = "images/Logo.png", alt = "Logo", attrs = {
                    classes("mdc-icon-button", AppStylesheet.image)
                    onClick { }
                }
            )
        }, navButtons = {
            Button(
                attrs = {
                    classes("mdc-button", "mdc-button--raised", "mdc-top-app-bar__navigation-icon")
                    onClick { onCategorySummaryButton() }
                }
            ) {
                Span(
                    attrs = {
                        classes("mdc-button__label")
                    }
                ) {
                    Text("Categories")
                }
            }
            Button(
                attrs = {
                    classes("mdc-button", "mdc-button--raised", "mdc-top-app-bar__navigation-icon")
                    onClick { onSettingsButton() }
                }
            ) {
                Span(
                    attrs = {
                        classes("mdc-button__label")
                    }
                ) {
                    Text("Settings")
                }
            }
        })

    MainFlexContainer {
        
        Div { DashboardData(categoryList, entryList, onEntryOverviewButton) }
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
fun DashboardData(categoryList: List<Category>, entryList: List<Entry>, onEntry: (id:Int) -> Unit) {
    console.log("Category $categoryList and Entry $entryList")
    var focusedCategory by remember { mutableStateOf(-1) } //Variable from -1 (all) to categoryList.size
    console.log("Focus:${focusedCategory}")
    fun changeFocusedCategory(increase: Boolean): Int {
        var newFocus = focusedCategory
        if (increase) newFocus++
        else newFocus--
        newFocus =
            when {
                (newFocus) < -1 -> -1
                (newFocus > categoryList.size) -> categoryList.size
                else -> {
                    newFocus
                }
            }
        return newFocus
    }

    //if (entryList.isNotEmpty()) {
        when (focusedCategory) {
            //Overall View
            -1 -> {
                var everyBudgetTogether = 0f
                for (category in categoryList) {
                    everyBudgetTogether += category.budget
                }
                val fakeOverallBudget =
                    Category(0, "Overall", "111111", Category.Image.DEFAULT, everyBudgetTogether)
                SwipeContainer (
                    content = {BudgetBar(fakeOverallBudget, entryList)}, //Every CategoryBudget with every Entry's Budget
                    onFocusCategoryChange = {focusedCategory = changeFocusedCategory(it)},
                    leftOn = false
                )

                EntryList(entryList, categoryList, onEntry) //List of Every Entry
            }
            //Normal Category View
            in categoryList.indices -> {
                val filteredEntryList =
                    entriesFromCategory(entryList, categoryList[focusedCategory].id)
                SwipeContainer (
                    content = {BudgetBar(categoryList[focusedCategory], filteredEntryList)}, //Every Category with their Entries' Budget
                    onFocusCategoryChange = {focusedCategory = changeFocusedCategory(it)}
                )
                EntryList(
                    filteredEntryList,
                    listOf(categoryList[focusedCategory]),
                    onEntry
                ) //Only gives CategoryData of selected category, as everything else seems unnecessary
            }

            //No Category View
            categoryList.size -> {
                val filteredEntryList = entriesFromCategory(entryList, null)
                val fakeNoCategory =
                    Category(0, "No Category", "111111", Category.Image.DEFAULT, 0f)
                SwipeContainer (
                    content = {BudgetBar(fakeNoCategory, filteredEntryList)}, //"No Category" with their Entries' Budget
                    onFocusCategoryChange = {focusedCategory = changeFocusedCategory(it)},
                    rightOn = false
                )
                EntryList(
                    filteredEntryList,
                    emptyList(),
                    onEntry
                ) //Needs no categoryList, as they have no category
            }
        }
    /*} else {
        Div(attrs = {
            classes(
                "mdc-typography--headline5",
                AppStylesheet.text
            )
        }) { Text("No data to load") }
    }*/

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
fun SwipeContainer(content: @Composable () -> Unit, onFocusCategoryChange: (Boolean) -> Unit, leftOn: Boolean = true, rightOn:Boolean = true) {
    Div(
        attrs = {
            classes(AppStylesheet.flexContainer)
        }) {
        Div(attrs = {
            if(leftOn) {
                classes(AppStylesheet.imageFlexContainer, "mdc-button")
                onClick { onFocusCategoryChange(false) }
            }
            else{
                classes(AppStylesheet.imageFlexContainer)
                style{
                    paddingLeft(8.px)
                    paddingRight(8.px)
                }
            }
        }){
            if(leftOn) Icon("arrow_back_ios_new")
        }
        Div(attrs = { classes(AppStylesheet.budgetBarContainer) })
        {
            content()
        }
        Div(attrs = {
            if(rightOn) {
                classes(AppStylesheet.imageFlexContainer, "mdc-button")
                onClick { onFocusCategoryChange(true) }
            }
            else{
                classes(AppStylesheet.imageFlexContainer)
                style{
                    paddingLeft(8.px)
                    paddingRight(8.px)
                }
            }
        }) {
            if(rightOn) Icon("arrow_forward_ios_new")
        }
    }
}






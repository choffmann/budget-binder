package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.Icon
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.category.BudgetBar
import de.hsfl.budgetBinder.compose.entry.EntryList
import de.hsfl.budgetBinder.compose.entry.entriesFromCategory
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.UiState
import kotlinx.serialization.json.JsonNull.content
import org.jetbrains.compose.web.css.paddingLeft
import org.jetbrains.compose.web.css.paddingRight
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*


@Composable
fun DashboardView(
    categoriesState: State<Any>,
    entriesState: State<Any>,
    onCategorySummaryButton: () -> Unit,
    onSettingsButton: () -> Unit,
    onEntryCreateButton: () -> Unit,
    onEntryEditButton: () -> Unit
) {
    val categoriesViewState by remember { categoriesState }
    val entriesViewState by remember { entriesState }
    var categoryList by remember { mutableStateOf<List<Category>>(emptyList()) }
    var entryList by remember { mutableStateOf<List<Entry>>(emptyList()) }

    MainFlexContainer {
        H1 { Text("DashboardView") }
        Button(attrs = {
            onClick { onSettingsButton() }
        }) {
            Text("Open Settings")
        }
        Button(attrs = {
            onClick { onCategorySummaryButton() }
        }) {
            Text("Open Category List (Summary of every Category)")
        }
        Button(attrs = {
            onClick { onEntryCreateButton() }
        }) {
            Text("Create Entry")
        }
        Button(attrs = {
            onClick { onEntryEditButton() }
        }) {
            Text("Edit Entry (Needs to be there for every Entry shown)")
        }
        Div {
            DashboardData(categoryList, entryList)
        }
        CreateNewEntryButton(onEntryCreateButton)
    }
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
            Text((categoriesViewState as UiState.Error).error)
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
            Text((entriesViewState as UiState.Error).error)
        }
        is UiState.Loading -> {
            //CircularProgressIndicator()
        }
    }


}

@Composable
fun DashboardData(categoryList: List<Category>, entryList: List<Entry>) {
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

    if (entryList.isNotEmpty()) {
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

                EntryList(entryList, categoryList) //List of Every Entry
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
                    listOf(categoryList[focusedCategory])
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
                    emptyList()
                ) //Needs no categoryList, as they have no category
            }
        }
    } else {
        //TODO: Show something like: NO DATA TO SHOW!
    }

}


@Composable
fun CreateNewEntryButton(onEntryCreateButton: () -> Unit) { //TODO: endlich mal schön machen!
    Button(attrs = {
        classes("mdc-fab","mdc-fab--touch")
        onClick { onEntryCreateButton() }
    }) {
        Div(attrs= {classes("mdc-fab__ripple")} )
        Icon("add")
        Div(attrs= {classes("mdc-fab__touch")} )
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






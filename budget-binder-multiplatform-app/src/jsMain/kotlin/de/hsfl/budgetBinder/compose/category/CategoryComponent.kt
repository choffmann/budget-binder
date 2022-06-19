package de.hsfl.budgetBinder.compose.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Constants.DEFAULT_CATEGORY
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.viewmodel.CategoryViewModel
import di
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.Rect
import org.jetbrains.compose.web.svg.Svg
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun CategoryComponent(screenState: MutableState<Screen>) {
    // Old => CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    // Use rememberCoroutineScope()
    val scope = rememberCoroutineScope()

    // Don't do this like here, use Kodein
    /* val di = localDI()
    val getAllCategoriesUseCase: GetAllCategoriesUseCase by di.instance()
    val createCategoryUseCase: CreateCategoryUseCase by di.instance()
    val getCategoryByIdUseCase: GetCategoryByIdUseCase by di.instance()
    val changeCategoryByIdUseCase: ChangeCategoryByIdUseCase by di.instance()
    val deleteCategoryByIdUseCase: DeleteCategoryByIdUseCase by di.instance()
    val getAllEntriesByCategoryUseCase: GetAllEntriesByCategoryUseCase by di.instance()
    val categoryViewModel = CategoryViewModel(getAllCategoriesUseCase, getCategoryByIdUseCase,createCategoryUseCase, changeCategoryByIdUseCase, deleteCategoryByIdUseCase, getAllEntriesByCategoryUseCase, scope)
    val viewState = categoryViewModel.state.collectAsState(scope)*/

    val viewModel: CategoryViewModel by di.instance()
    val viewState = viewModel.state.collectAsState(scope.coroutineContext)

    when (screenState.value) {
        is Screen.CategoryCreate -> CategoryCreateView(
            state = viewState,
            onCreateCategoryButtonPressed = { name, color, image, budget ->
                categoryViewModel.createCategory(Category.In(name, color.drop(1), image, budget))
                screenState.value = Screen.CategorySummary },
            onChangeToDashboard = { screenState.value = Screen.Dashboard },
            onChangeToSettings = { screenState.value = Screen.Settings },
            onChangeToCategory = { screenState.value = Screen.CategorySummary },
        )
        is Screen.CategorySummary -> {
            CategorySummaryView(
                state = viewState,
                onCategoryCreateButton = { screenState.value = Screen.CategoryCreate },
                onEditButton = { id -> screenState.value = Screen.CategoryEdit(id) },
                onDeleteButton = { id -> categoryViewModel.removeCategory(id) },
                onChangeToDashboard = { screenState.value = Screen.Dashboard },
                onChangeToSettings = { screenState.value = Screen.Settings },
                onChangeToCategory = { screenState.value = Screen.CategorySummary },
            )
            categoryViewModel.getAllCategories()
        }
        is Screen.CategoryEdit -> {
            CategoryEditView(
                state = viewState,
                onEditCategoryButtonPressed = { name, color, image, budget ->
                    categoryViewModel.changeCategory(
                        Category.Patch(name, color.drop(1), image, budget),
                        (screenState.value as Screen.CategoryEdit).id
                    ); screenState.value = Screen.CategorySummary },
                onChangeToDashboard = { screenState.value = Screen.Dashboard },
                onChangeToSettings = { screenState.value = Screen.Settings },
                onChangeToCategory = { screenState.value = Screen.CategorySummary },
            )
            categoryViewModel.getCategoryById((screenState.value as Screen.CategoryEdit).id)
        }
        is Screen.CategoryCreateOnRegister -> CategoryCreateOnRegisterView(
            state = viewState,
            onFinishedButton = {
                screenState.value = Screen.Dashboard
            } //Should go back to the previous Screen, which could be CategorySummary or EntryCreate.
        )
        else -> {}
    }
}

fun categoryIdToCategory(category_id: Int?, categoryList: List<Category>): Category {
    for (category in categoryList) {
        if (category.id == category_id) return category
    }
    return DEFAULT_CATEGORY //If the category wasn't found (or is set to no category) return default
}

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun BudgetBar(category: Category, entryList: List<Entry>) {
    //category = Category we want to show
    //entryList = List of entries
    //width and height are for aspect ratio - tries to fill out wherever its in, so its more like
    val width = 20
    val height = 2
    val budget = category.budget
    var usedBudget = 0f
    for (entry in entryList) {
        usedBudget -= entry.amount //Money spent negative, so we want to add the negative amount (- - = +)to usedBudget
    }
    H1(attrs = { classes("mdc-typography--headline4", AppStylesheet.flexContainer) }) {
        CategoryImageToIcon(category.image)
        Text("${category.name} - Budget")
    }

    Div {
        if (usedBudget <= budget && budget > 0) { //Normal not Spent Budget
            //Money Text
            MoneyTextDiv {
                Div(attrs = {
                    classes("mdc-typography--headline5")
                }) { Text(usedBudget.toString() + "€") }
                Div(attrs = { classes("mdc-typography--headline5") }) { Text(budget.toString() + "€") }
            }
            Svg(viewBox = "0 0 $width $height") {
                Rect(x = 0, y = 0, width = width, height = height, {
                    attr("fill", Color.lightgray.toString())
                })
                if (0 < usedBudget) // If there is used budget, draw it
                    Rect(x = 0, y = 0, width = usedBudget / budget * width, height = height, {
                        attr("fill", "#" + category.color)
                    })
            }
        } else if (usedBudget > budget && budget > 0) { //Over Budget
            MoneyTextDiv {
                Div(attrs = { classes("mdc-typography--headline5") }) { Text("Budget limit for " + category.name + " reached! " + usedBudget.toString() + "€ of " + budget.toString() + "€ Budget spent") }
            }
            Svg(viewBox = "0 0 $width $height") {
                Rect(x = 0, y = 0, width = width, height = height, {
                    attr("fill", "#b00020")
                })
            }
        } else if (budget <= 0f) { //No Category View or other unpredictable case (or no categories, overall screen)
            MoneyTextDiv {
                Div(attrs = { classes("mdc-typography--headline5") }) { Text(usedBudget.toString() + "€ spent") }
            }
            Svg(viewBox = "0 0 $width $height") {
                Rect(x = 0, y = 0, width = width, height = height, {
                    attr("fill", "#" + category.color)
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


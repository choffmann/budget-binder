package de.hsfl.budgetBinder.compose.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.common.StateManager.categoryList
import de.hsfl.budgetBinder.domain.usecase.*
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.viewmodel.CategoryViewModel
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
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val getAllCategoryUseCase: GetAllCategoriesUseCase by di.instance()
    val createCategoryUseCase: CreateCategoryUseCase by di.instance()
    val getCategoryByIdUseCase: GetCategoryByIdUseCase by di.instance()
    val changeCategoryByIdUseCase: ChangeCategoryByIdUseCase by di.instance()
    val deleteCategoryByIdUseCase: DeleteCategoryByIdUseCase by di.instance()
    val getAllEntriesByCategoryUseCase: GetAllEntriesByCategoryUseCase by di.instance()
    val categoryViewModel = CategoryViewModel(getAllCategoryUseCase, getCategoryByIdUseCase,createCategoryUseCase, changeCategoryByIdUseCase, deleteCategoryByIdUseCase, getAllEntriesByCategoryUseCase, scope)
    val viewState = categoryViewModel.state.collectAsState(scope)

    when (screenState.value) {
        Screen.CategoryCreate -> CategoryCreateView(
            state = viewState,
            onBackButton = { screenState.value = Screen.CategorySummary}
        )
        Screen.CategorySummary -> CategorySummaryView(
            state = viewState,
            onBackButton = { screenState.value = Screen.Dashboard},
            onEditButton = { screenState.value = Screen.CategoryEdit},
            onCategoryCreateButton = { screenState.value = Screen.CategoryCreate},
        )
        Screen.CategoryEdit -> CategoryEditView(
            state = viewState,
            onBackButton = { screenState.value = Screen.CategorySummary}
        )
        else -> {}
    }
}

//Should be put in own File
@Composable
fun Icon(category: Category){
    val imagePath =
        when (category.image) {
            Category.Image.DEFAULT -> "cart.png"
            Category.Image.SHOPPING -> "cart.png"
        }
    Img(imagePath, "imagePath", attrs = {classes("mdc-icon-button")})
}
@Composable
fun Icon(id: Int?){
    var categoryImage = Category.Image.DEFAULT
    if (id != null) {
        categoryImage = categoryList[id].image
    }
    val imagePath =
        when (categoryImage) {
            Category.Image.DEFAULT -> "cart.png"
            Category.Image.SHOPPING -> "cart.png"
        }
    Img(imagePath, "imagePath", attrs = {
        classes("mdc-icon-button")
        style { padding(0.px) }
    })
}

@OptIn(ExperimentalComposeWebSvgApi::class)
@Composable
fun Bar(category: Category, entryList: List<Entry>){
    //width and height are for aspect ratio - tries to fill out wherever its in, so its more like
    val width = 200
    val height = 80
    val budget = category.budget
    var usedBudget = 0f
    for (entry in entryList) {
        usedBudget+= entry.amount
    }
    Div{
        if (usedBudget < budget) {
            //Money Text
            Div(attrs={style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent("space-between"))
            }}){
                Div{Text(usedBudget.toString()+"€")}
                Div{Text(budget.toString()+"€")}
            }
            //Bar
            Svg(viewBox = "0 0 $width $height"){//For aspect ratio - tries to fill out wherever its in
                Rect(x = 0, y = 0, width = width, height = height, {
                    attr("fill", Color.lightgray.toString())
                })
                Rect(x = 0, y = 0, width = usedBudget/budget*width, height = height, {
                    attr("fill", Color.darkred.toString())
                })
            }
        }
        else{
            //SpentBudget Text
            Div(attrs={style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent("center"))
            }}){
                Div{Text("Budget limit for "+category.name+" reached! "+usedBudget.toString()+"€ of "+budget.toString()+"€ Budget spent")}
            }
            //Bar
            Svg(viewBox = "0 0 $width $height"){//For aspect ratio - tries to fill out wherever its in
                Rect(x = 0, y = 0, width = width, height = height, {
                    attr("fill", Color.red.toString())
                })
            }
        }
    }
}


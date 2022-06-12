package de.hsfl.budgetBinder.compose.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Constants.DEFAULT_CATEGORY
import de.hsfl.budgetBinder.common.Entry
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
    val getAllCategoriesUseCase: GetAllCategoriesUseCase by di.instance()
    val createCategoryUseCase: CreateCategoryUseCase by di.instance()
    val getCategoryByIdUseCase: GetCategoryByIdUseCase by di.instance()
    val changeCategoryByIdUseCase: ChangeCategoryByIdUseCase by di.instance()
    val deleteCategoryByIdUseCase: DeleteCategoryByIdUseCase by di.instance()
    val getAllEntriesByCategoryUseCase: GetAllEntriesByCategoryUseCase by di.instance()
    val categoryViewModel = CategoryViewModel(getAllCategoriesUseCase, getCategoryByIdUseCase,createCategoryUseCase, changeCategoryByIdUseCase, deleteCategoryByIdUseCase, getAllEntriesByCategoryUseCase, scope)
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
        Screen.CategoryCreateOnRegister -> CategoryCreateOnRegisterView(
            state = viewState,
            onFinishedButton = { screenState.value = Screen.Dashboard} //Should go back to the previous Screen, which could be CategorySummary or EntryCreate.
        )
        else -> {}
    }
}

//Should be put in own File
@Composable
fun Icon(category: Category){
    val imageName =
        when (category.image) {
            Category.Image.SHOPPINGCART -> "shopping_cart"
            Category.Image.SHOPPINGBASKET -> "shopping_basket"
            Category.Image.CHECKMARK -> "done"
            Category.Image.WRONG -> "dangerous"
            Category.Image.HOME -> "home"
            Category.Image.FOOD -> "bakery_dining"
            Category.Image.FASTFOOD -> "bakery_dining"
            Category.Image.RESTAURANT -> "restaurant"
            Category.Image.FAMILY -> "people"
            Category.Image.MONEY -> "payments"
            Category.Image.HEALTH -> "health_and_safety"
            Category.Image.MEDICATION -> "medication"
            Category.Image.INVEST -> "query_stats"
            Category.Image.SPORT -> "sports_soccer"
            Category.Image.CLOTH -> "checkroom"
            Category.Image.GIFT -> "redeem"
            Category.Image.WEALTH -> "monetization_on"
            Category.Image.FLOWER -> "local_florist"
            Category.Image.PET -> "pets"
            Category.Image.BILLS -> "receipt"
            Category.Image.KEYBOARD-> "redeem"
            Category.Image.PRINTER-> "print"
            Category.Image.WATER -> "water_drop"
            Category.Image.FIRE -> "fire"
            Category.Image.STAR -> "grade"
            Category.Image.SAVINGS -> "savings"
            Category.Image.CAR -> "minor_crash"
            Category.Image.BIKE -> "pedal_bike"
            Category.Image.TRAIN -> "directions_transit"
            Category.Image.MOPED-> "moped"
            Category.Image.MOTORCYCLE -> "two_wheeler"
            Category.Image.ELECTRONICS -> "electrical_services"
            Category.Image.BOOK -> "book"
            Category.Image.FLIGHT -> "flight_takeoff"
            Category.Image.WORK -> "work"
            Category.Image.MOON -> "nightlight_round"
            Category.Image.LOCK -> "https"
            Category.Image.PHONE -> "perm_phone_msg"
            Category.Image.STORE -> "store_mall_directory"
            Category.Image.BAR -> "nightlife"
            Category.Image.FOREST -> "forest"
            Category.Image.HARDWARE -> "hardware"
            Category.Image.PEST -> "pest_control"
            else -> {""} //Default will be this
        }
    Span(
        attrs = {
            classes("material-icons")
        }
    ) {Text(imageName)}
}
fun categoryIdToCategory(category_id: Int?,categoryList: List<Category>): Category {
    for (category in categoryList){
        if (category.id == category_id) return category
    }
    return DEFAULT_CATEGORY //If the category wasn't found (or is set to no category) return default
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


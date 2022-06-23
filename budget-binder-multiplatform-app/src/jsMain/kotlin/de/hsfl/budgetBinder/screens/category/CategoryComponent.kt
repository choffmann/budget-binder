package de.hsfl.budgetBinder.screens.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Constants.DEFAULT_CATEGORY
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.category.CategoryViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryEvent
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.ExperimentalComposeWebSvgApi
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.Rect
import org.jetbrains.compose.web.svg.Svg
import org.kodein.di.instance

@Composable
fun CategoryComponent() {
    val viewModel: CategoryViewModel by di.instance()
    val routerFlow: RouterFlow by di.instance()
    /*when (routerFlow.state.value) {
        is Screen.Category.Summary -> CategorySummaryView()
        is Screen.Category.Detail -> CategoryDetailView()
        is Screen.Category.Edit -> CategoryEditView()
        is Screen.Category.Create -> CategoryCreateView()
    }*/
}

fun categoryIdToCategory(category_id: Int?, categoryList: List<Category>): Category {
    for (category in categoryList) {
        if (category.id == category_id) return category
    }
    return DEFAULT_CATEGORY //If the category wasn't found (or is set to no category) return default
}




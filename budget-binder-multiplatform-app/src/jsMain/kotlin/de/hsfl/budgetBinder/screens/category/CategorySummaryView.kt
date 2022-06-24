package de.hsfl.budgetBinder.screens.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.CategoryList
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.summary.CategorySummaryEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.summary.CategorySummaryViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun CategorySummaryView(
) {
    val viewModel: CategorySummaryViewModel by di.instance()
    val categoryList by viewModel.categoryList.collectAsState()

    //LifeCycle
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(CategorySummaryEvent.LifeCycle(LifecycleEvent.OnLaunch))
        viewModel.eventFlow.collectLatest {}
        console.log("CategorySummary Came to Life!")
    }

    //Webpage Content
    H1(attrs = { classes(AppStylesheet.h1) }) { Text(" Category Summary") }
    Button(attrs = {
        classes("mdc-button", "mdc-button--raised")
        onClick { viewModel.onEvent(CategorySummaryEvent.OnCategoryCreate) }
        style { margin(2.percent) }
    }) {
        Text("Create Category")
    }
    CategoryList(
        categoryList
    ) { id -> viewModel.onEvent(CategorySummaryEvent.OnCategory(id)) }
}


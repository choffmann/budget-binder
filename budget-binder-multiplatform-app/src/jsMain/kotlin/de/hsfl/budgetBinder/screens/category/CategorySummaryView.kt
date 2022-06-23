package de.hsfl.budgetBinder.screens.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.CategoryList
import de.hsfl.budgetBinder.presentation.viewmodel.category.summary.CategorySummaryEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.summary.CategorySummaryViewModel
import di
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun CategorySummaryView(
) {
    val viewModel: CategorySummaryViewModel by di.instance()
    val categoryList by viewModel.categoryList.collectAsState()

    H1(
        attrs = {
            style { margin(2.percent) }
        }
    ) { Text(" Category Summary") }
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


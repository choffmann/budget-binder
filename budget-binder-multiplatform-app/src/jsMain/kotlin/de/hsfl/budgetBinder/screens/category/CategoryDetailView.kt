package de.hsfl.budgetBinder.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.hsfl.budgetBinder.compose.CategoryDetailed
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailViewModel
import di
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text
import org.kodein.di.instance

@Composable
fun CategoryDetailView() {
    val viewModel: CategoryDetailViewModel by di.instance()
    val category by viewModel.categoryState.collectAsState()

    //LifeCycle
    LaunchedEffect(key1 = true) {
        viewModel.onEvent(CategoryDetailEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }

    //Webpage Content
    H1(attrs = { classes(AppStylesheet.h1) })  { Text(" Category Detailed") }
    CategoryDetailed(
        category,
        { viewModel.onEvent(CategoryDetailEvent.OnEdit) },
        { viewModel.onEvent(CategoryDetailEvent.OnDelete) },
        { viewModel.onEvent(CategoryDetailEvent.OnBack) }
    )

}

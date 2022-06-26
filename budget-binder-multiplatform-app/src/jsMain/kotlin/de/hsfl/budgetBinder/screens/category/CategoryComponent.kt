package de.hsfl.budgetBinder.screens.category

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.NavBar
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import di
import org.kodein.di.instance

@Composable
fun CategoryComponent() {
    val routerFlow: RouterFlow by di.instance()

    //Webpage content
    NavBar {}
    MainFlexContainer {
        when (routerFlow.state.value) {
            is Screen.Category.Summary -> CategorySummaryView()
            is Screen.Category.Detail -> CategoryDetailView()
            is Screen.Category.Edit -> CategoryEditView()
            is Screen.Category.Create -> CategoryCreateView()
            else -> {}
        }
    }
}



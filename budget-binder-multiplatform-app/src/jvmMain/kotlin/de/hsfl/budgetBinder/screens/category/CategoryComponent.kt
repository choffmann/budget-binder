package de.hsfl.budgetBinder.screens.category

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import org.kodein.di.instance

@Composable
fun CategoryComponent() {
    val routerFlow: RouterFlow by di.instance()
    when (routerFlow.state.value) {
        is Screen.Category.Summary -> CategorySummary()
        is Screen.Category.Detail -> CategoryDetailView()
        is Screen.Category.Edit -> CategoryEditView()
        is Screen.Category.Create -> CategoryCreateView()
    }
}

package de.hsfl.budgetBinder.screens.category

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow

@Composable
fun CategoryComponent() {
    when (RouterFlow.state.value) {
        is Screen.Category.Summary -> CategorySummary()
        is Screen.Category.Detail -> CategoryDetailView()
        else -> {}
    }
}

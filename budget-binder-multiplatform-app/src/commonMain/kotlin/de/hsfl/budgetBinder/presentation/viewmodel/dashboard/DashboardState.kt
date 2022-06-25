package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry

data class DashboardState(
    val categoryList: List<Category> = listOf(),
    val focusedCategory: DashboardFocusedCategoryState = DashboardFocusedCategoryState(),
    val entryList: List<DashboardEntryState> = emptyList(),
    val oldEntriesList: Map<String, DashboardState> = mapOf()
)

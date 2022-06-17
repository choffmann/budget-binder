package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry

data class DashboardState(
    val hasPrev: Boolean = false,
    val hasNext: Boolean = true,
    val category: Category = Category(0, "Overall", "111111", Category.Image.DEFAULT, 0f),
    val entryList: List<Entry> = emptyList(),
    val spendBudgetOnCurrentCategory: Float = 0f
)

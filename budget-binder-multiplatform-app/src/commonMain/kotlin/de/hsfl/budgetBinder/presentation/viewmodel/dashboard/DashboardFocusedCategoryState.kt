package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

import de.hsfl.budgetBinder.common.Category

data class DashboardFocusedCategoryState(
    val category: Category = Category(0, "Overall", "111111", Category.Image.DEFAULT, 0f),
    val spendBudget: Float = 0f,
    val hasPrev: Boolean = false,
    val hasNext: Boolean = true,
)

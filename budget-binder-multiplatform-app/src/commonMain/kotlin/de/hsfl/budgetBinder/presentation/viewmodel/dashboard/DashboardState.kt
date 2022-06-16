package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry

data class DashboardState(
    val categoryList: List<Category> = emptyList(),
    val entryList: List<Entry> = emptyList(),
    val focusedCategory: Category = Category(0, "Allgemein", "FFFFFF", Category.Image.DEFAULT, 0f)
)

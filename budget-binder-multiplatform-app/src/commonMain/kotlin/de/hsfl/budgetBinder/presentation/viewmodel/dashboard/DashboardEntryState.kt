package de.hsfl.budgetBinder.presentation.viewmodel.dashboard

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry

data class DashboardEntryState(
    val entry: Entry,
    val categoryImage: Category.Image = Category.Image.DEFAULT
)

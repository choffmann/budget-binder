package de.hsfl.budgetBinder.presentation.viewmodel.entry

import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry

data class EntryState(
    val selectedEntry: Entry = Entry(0,"",0f,false,null),
    val categoryList: List<Category> = listOf(Category(0,"No category","ffffff",Category.Image.DEFAULT,0f))
)

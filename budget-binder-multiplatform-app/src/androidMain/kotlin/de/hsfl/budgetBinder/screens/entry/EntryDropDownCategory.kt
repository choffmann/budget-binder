package de.hsfl.budgetBinder.screens.entry

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.screens.category.CategoryListItem
import de.hsfl.budgetBinder.screens.category.toColor

@Composable
actual fun EntryDropDownCategory(
    modifier: Modifier,
    categoryList: List<Category>,
    expand: Boolean,
    onDismiss: () -> Unit,
    onItemClicked: (Int) -> Unit
) {
    DropdownMenu(expanded = expand, onDismissRequest = onDismiss) {
        categoryList.forEachIndexed { index, category ->
            DropdownMenuItem(onClick = {
                onItemClicked(index)
            }) {
                CategoryListItem(
                    name = category.name,
                    budget = category.budget.toString(),
                    icon = category.image,
                    color = category.color.toColor("af")
                )
            }
        }
    }
}

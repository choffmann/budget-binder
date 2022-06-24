package de.hsfl.budgetBinder.screens.entry

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.common.Category

@Composable
actual fun EntryDropDownCategory(
    modifier: Modifier,
    categoryList: List<Category>,
    expand: Boolean,
    onDismiss: () -> Unit,
    onItemClicked: (Int) -> Unit
) {
}

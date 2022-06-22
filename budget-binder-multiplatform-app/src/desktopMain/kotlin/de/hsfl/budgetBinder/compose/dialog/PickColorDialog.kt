package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.hsfl.budgetBinder.common.Category

@Composable
actual fun PickColorDialog(
    openDialog: Boolean,
    categoryName: String,
    categoryColor: String,
    categoryImage: Category.Image,
    categoryBudget: Float,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
}

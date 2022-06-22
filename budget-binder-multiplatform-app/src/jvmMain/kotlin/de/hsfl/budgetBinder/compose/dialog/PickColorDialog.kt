package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.common.Category
import androidx.compose.ui.graphics.Color

@Composable
expect fun PickColorDialog(
    openDialog: Boolean,
    categoryName: String,
    categoryColor: String,
    categoryImage: Category.Image,
    categoryBudget: Float,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
)

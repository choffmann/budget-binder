package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.common.Category
import androidx.compose.ui.graphics.Color

@Composable
expect fun PickIconDialog(
    openDialog: Boolean,
    categoryName: String,
    categoryBudget: Float,
    onConfirm: (Category.Image) -> Unit,
    onDismiss: () -> Unit,
    selectColor: Color
)

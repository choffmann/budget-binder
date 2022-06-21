package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.hsfl.budgetBinder.common.Category

@Composable
actual fun PickIconDialog(
    openDialog: Boolean,
    categoryName: String,
    categoryBudget: Float,
    onConfirm: (Category.Image) -> Unit,
    onDismiss: () -> Unit,
    selectColor: Color
) {
}

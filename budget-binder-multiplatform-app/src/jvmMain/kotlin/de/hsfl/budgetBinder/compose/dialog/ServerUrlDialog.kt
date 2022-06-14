package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.runtime.Composable

@Composable
expect fun ServerUrlDialog(
    value: String,
    onValueChange: (String) -> Unit,
    openDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
)
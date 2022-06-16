package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.runtime.Composable

@Composable
expect fun DeleteUserDialog(
    openDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
)

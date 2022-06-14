package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@Composable
actual fun ServerUrlDialog(
    value: String,
    onValueChange: (String) -> Unit,
    openDialog: Boolean,
    onConfirm: () -> Unit,
    onDissmiss: () -> Unit
) {

}
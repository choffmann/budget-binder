package de.hsfl.budgetBinder.compose.user

import androidx.compose.runtime.*


@Composable
fun UserView(
    state: State<Any>,
    onUpdate: () -> Unit,
    onLogout: () -> Unit
) {
    val viewState by remember { state }
}

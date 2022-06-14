package de.hsfl.budgetBinder.compose.icon

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
actual fun AppIcon(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("budgetbinder.svg"), contentDescription = null)
}
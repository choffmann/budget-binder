package de.hsfl.budgetBinder.compose.icon

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import de.hsfl.budgetBinder.android.R

@Composable
actual fun AppIcon(modifier: Modifier) {
    Image(modifier = modifier, imageVector = ImageVector.vectorResource(id = R.drawable.ic_budgetbinder), contentDescription = null)
}
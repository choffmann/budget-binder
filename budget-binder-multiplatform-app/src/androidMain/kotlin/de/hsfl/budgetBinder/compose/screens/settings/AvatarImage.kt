package de.hsfl.budgetBinder.compose.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import de.hsfl.budgetBinder.android.R

@Composable
actual fun AvatarImage(modifier: Modifier) {
    val avatar = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.user_avatar))
    Image(modifier = modifier, painter = avatar, contentDescription = null)
}
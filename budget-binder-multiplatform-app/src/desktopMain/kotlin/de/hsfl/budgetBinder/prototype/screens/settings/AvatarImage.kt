package de.hsfl.budgetBinder.prototype.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
actual fun AvatarImage(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("undraw_profile_pic_ic-5-t.svg"), contentDescription = null)
}
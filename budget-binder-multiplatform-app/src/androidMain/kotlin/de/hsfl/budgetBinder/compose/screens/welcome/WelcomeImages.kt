package de.hsfl.budgetBinder.compose.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import de.hsfl.budgetBinder.android.R

@Composable
actual fun ImageWelcomeScreen1(modifier: Modifier) {
    val welcomeDraw = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.welcome_screen1_undraw))
    Image(modifier = modifier, painter = welcomeDraw, contentDescription = null)
}

@Composable
actual fun ImageWelcomeScreen2(modifier: Modifier) {
    val welcomeDraw = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.welcome_screen2_undraw))
    Image(modifier = modifier, painter = welcomeDraw, contentDescription = null)
}

@Composable
actual fun ImageWelcomeScreen3(modifier: Modifier) {
    val welcomeDraw = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.welcome_getstarted_undraw))
    Image(modifier = modifier, painter = welcomeDraw, contentDescription = null)
}
package de.hsfl.budgetBinder.compose.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
actual fun ImageWelcomeScreen1(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("welcomeScreen/undraw_welcome_re_h3d9.svg"), contentDescription = null)
}

@Composable
actual fun ImageWelcomeScreen2(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("welcomeScreen/undraw_savings_re_eq4w.svg"), contentDescription = null)
}

@Composable
actual fun ImageWelcomeScreen3(modifier: Modifier) {
    Image(modifier = modifier, painter = painterResource("welcomeScreen/undraw_awesome_rlvy.svg"), contentDescription = null)
}
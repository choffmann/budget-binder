package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.compose.StateManager.screenState
import de.hsfl.budgetBinder.compose.screens.home.HomeComponent
import de.hsfl.budgetBinder.compose.screens.settings.SettingsComponent
import de.hsfl.budgetBinder.compose.screens.welcome.LoginComponent
import de.hsfl.budgetBinder.compose.screens.welcome.RegisterComponent
import de.hsfl.budgetBinder.compose.screens.welcome.WelcomeComponent
import de.hsfl.budgetBinder.presentation.Screen

@Composable
fun Router() {
    when (screenState.value) {
        is Screen.Welcome -> WelcomeComponent()
        is Screen.Register -> RegisterComponent()
        is Screen.Login -> LoginComponent()
        is Screen.Home -> HomeComponent()
        is Screen.Settings -> SettingsComponent()
        is Screen.Categories -> {}
    }
}
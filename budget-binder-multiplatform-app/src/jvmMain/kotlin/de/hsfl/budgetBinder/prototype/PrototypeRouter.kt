package de.hsfl.budgetBinder.prototype

import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.prototype.StateManager.screenState
import de.hsfl.budgetBinder.prototype.screens.categories.CategoriesComponent
import de.hsfl.budgetBinder.prototype.screens.home.HomeComponent
import de.hsfl.budgetBinder.prototype.screens.screen1.Screen1Component
import de.hsfl.budgetBinder.prototype.screens.screen2.Screen2Component
import de.hsfl.budgetBinder.prototype.screens.settings.SettingsComponent
import de.hsfl.budgetBinder.prototype.screens.welcome.WelcomeComponent

@Composable
fun Router() {
    when (screenState.value) {
        is PrototypeScreen.Welcome -> WelcomeComponent()
        is PrototypeScreen.Home -> HomeComponent()
        is PrototypeScreen.Categories -> CategoriesComponent()
        is PrototypeScreen.Settings -> SettingsComponent()
        is PrototypeScreen.Screen1 -> Screen1Component()
        is PrototypeScreen.Screen2 -> Screen2Component()
    }
}
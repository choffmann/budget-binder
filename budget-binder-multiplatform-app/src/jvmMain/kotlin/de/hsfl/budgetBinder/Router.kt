package de.hsfl.budgetBinder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import de.hsfl.budgetBinder.screens.dashboard.DashboardComponent
import de.hsfl.budgetBinder.screens.login.LoginComponent
import de.hsfl.budgetBinder.screens.register.RegisterComponent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.screens.category.CategoryComponent
import de.hsfl.budgetBinder.screens.entry.EntryComponent
import de.hsfl.budgetBinder.screens.settings.SettingsView
import de.hsfl.budgetBinder.screens.welcome.WelcomeComponent
import org.kodein.di.instance

@Composable
fun Router() {
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState()
    when (screenState.value) {
        is Screen.Welcome -> WelcomeComponent()
        is Screen.Register -> RegisterComponent()
        is Screen.Login -> LoginComponent()
        is Screen.Dashboard -> DashboardComponent()
        is Screen.Settings.Menu, Screen.Settings.User, Screen.Settings.Server -> SettingsView()
        is Screen.Entry -> EntryComponent()
        is Screen.Category -> CategoryComponent()
        else -> {}
    }
}

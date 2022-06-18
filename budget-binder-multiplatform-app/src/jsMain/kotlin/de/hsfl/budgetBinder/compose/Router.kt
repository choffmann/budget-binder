package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import de.hsfl.budgetBinder.compose.category.CategoryComponent
import de.hsfl.budgetBinder.compose.dashboard.DashboardComponent
import de.hsfl.budgetBinder.compose.entry.EntryComponent
import de.hsfl.budgetBinder.compose.login.LoginComponent
import de.hsfl.budgetBinder.compose.register.RegisterComponent
import de.hsfl.budgetBinder.compose.settings.SettingsComponent
import de.hsfl.budgetBinder.presentation.Screen

@Composable
fun Router(screenState: MutableState<Screen>) {
    when (screenState.value) {
        is Screen.Welcome -> {}
        is Screen.Register -> RegisterComponent(screenState = screenState)
        is Screen.Login -> LoginComponent(screenState = screenState)
        is Screen.Dashboard -> DashboardComponent(screenState = screenState)
        is Screen.Settings, is Screen.SettingsChangeUserData -> SettingsComponent(screenState = screenState)
        is Screen.CategorySummary, is Screen.CategoryEdit, is Screen.CategoryCreate, Screen.CategoryCreateOnRegister
        -> CategoryComponent(screenState = screenState)
        is Screen.EntryCreate, is Screen.EntryEdit, is Screen.EntryOverview -> EntryComponent(screenState = screenState)
        else -> {}
    }
}

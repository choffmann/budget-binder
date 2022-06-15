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
        Screen._Welcome -> {}
        Screen.Register -> RegisterComponent(screenState = screenState)
        Screen.Login -> LoginComponent(screenState = screenState)
        Screen.Dashboard -> DashboardComponent(screenState = screenState)
        Screen._Settings, Screen.SettingsChangeUserData -> SettingsComponent(screenState = screenState)
        Screen.CategorySummary,Screen.CategoryEdit,Screen.CategoryCreate, Screen.CategoryCreateOnRegister -> CategoryComponent(screenState = screenState)
        Screen.EntryCreate, Screen.EntryEdit -> EntryComponent(screenState = screenState)
    }
}
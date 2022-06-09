package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import de.hsfl.budgetBinder.compose.categoryCreate.CategoryCreateComponent
import de.hsfl.budgetBinder.compose.categoryCreateOnRegister.CategoryCreateOnRegisterComponent
import de.hsfl.budgetBinder.compose.categoryEdit.CategoryEditComponent
import de.hsfl.budgetBinder.compose.categorySummary.CategorySummaryComponent
import de.hsfl.budgetBinder.compose.dashboard.DashboardComponent
import de.hsfl.budgetBinder.compose.entryCreate.EntryCreateComponent
import de.hsfl.budgetBinder.compose.entryEdit.EntryEditComponent
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
        is Screen.Settings -> SettingsComponent(screenState = screenState)
        is Screen.CategorySummary -> CategorySummaryComponent(screenState = screenState)
        is Screen.CategoryEdit -> CategoryEditComponent(screenState = screenState)
        is Screen.CategoryCreate -> CategoryCreateComponent(screenState = screenState)
        is Screen.CategoryCreateOnRegister -> CategoryCreateOnRegisterComponent(screenState = screenState)
        is Screen.EntryCreate -> EntryCreateComponent(screenState = screenState)
        is Screen.EntryEdit -> EntryEditComponent(screenState = screenState)

    }
}
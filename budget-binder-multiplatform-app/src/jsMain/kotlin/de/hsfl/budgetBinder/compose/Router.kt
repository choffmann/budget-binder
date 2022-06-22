package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import de.hsfl.budgetBinder.compose.dashboard.DashboardComponent
import de.hsfl.budgetBinder.compose.entry.EntryComponent
import de.hsfl.budgetBinder.compose.login.LoginComponent
import de.hsfl.budgetBinder.compose.register.RegisterComponent
import de.hsfl.budgetBinder.compose.settings.SettingsComponent
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import di
import org.jetbrains.compose.web.dom.Text
import org.kodein.di.instance

@Composable
fun Router() {
    val scope = rememberCoroutineScope()
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState(scope.coroutineContext)
    console.log(screenState.value)
    when (screenState.value) {
        is Screen.Welcome -> {}
        is Screen.Register -> RegisterComponent()
        is Screen.Login -> LoginComponent()
        is Screen.Dashboard -> DashboardComponent()
        is Screen.Settings -> SettingsComponent()
        is Screen.Entry -> EntryComponent()
        is Screen.CategorySummary, is Screen.CategoryEdit, is Screen.CategoryCreate, is Screen.CategoryCreateOnRegister
        -> Text("Old Category") //CategoryComponent(screenState = screenState)
        is Screen.EntryCreate, is Screen.EntryEdit, is Screen.EntryOverview
        -> Text("Old Entry")//EntryComponent(screenState = screenState)
        is Screen.Category -> Text("New Category")
        else -> {
            Text("No known Screen! Check if the screen you're trying to reach is in the ScreenRouter")
        }
    }
}

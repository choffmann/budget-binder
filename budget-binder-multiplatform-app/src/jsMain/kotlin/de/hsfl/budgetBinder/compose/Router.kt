package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.screens.category.CategoryComponent
import de.hsfl.budgetBinder.screens.dashboard.DashboardComponent
import de.hsfl.budgetBinder.screens.entry.EntryComponent
import de.hsfl.budgetBinder.screens.login.LoginComponent
import de.hsfl.budgetBinder.screens.register.RegisterComponent
import de.hsfl.budgetBinder.screens.settings.SettingsComponent
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
        is Screen.Category -> CategoryComponent()
        is Screen.CategorySummary, is Screen.CategoryEdit, is Screen.CategoryCreate, is Screen.CategoryCreateOnRegister
        -> Text("Old Category") //CategoryComponent(screenState = screenState)
        is Screen.EntryCreate, is Screen.EntryEdit, is Screen.EntryOverview
        -> Text("Old Entry")//EntryComponent(screenState = screenState)
        else -> {
            Text("No known Screen! Check if the screen you're trying to reach is in the ScreenRouter")
        }
    }
}

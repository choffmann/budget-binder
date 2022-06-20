package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import de.hsfl.budgetBinder.compose.category.CategoryComponent
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
    when (screenState.value) {
        is Screen.Welcome -> {}
        is Screen.Register -> RegisterComponent()
        is Screen.Login -> LoginComponent()
        is Screen.Dashboard -> Text("Dashboard")//DashboardComponent()
        //is Screen.Settings, is Screen.SettingsChangeUserData -> SettingsComponent(screenState = screenState)
        //is Screen.CategorySummary, is Screen.CategoryEdit, is Screen.CategoryCreate, is Screen.CategoryCreateOnRegister -> CategoryComponent(screenState = screenState)
        //is Screen.EntryCreate, is Screen.EntryEdit, is Screen.EntryOverview -> EntryComponent(screenState = screenState)
        else -> {}
    }
}

package de.hsfl.budgetBinder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import de.hsfl.budgetBinder.screens.dashboard.DashboardComponent
import de.hsfl.budgetBinder.screens.login.LoginComponent
import de.hsfl.budgetBinder.screens.register.RegisterComponent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.Screen
import org.kodein.di.instance

@Composable
fun Router() {
    val scope = rememberCoroutineScope()
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState(scope.coroutineContext)
    when (screenState.value) {
        is Screen.Register -> RegisterComponent()
        is Screen.Login -> LoginComponent()
        is Screen.Dashboard -> DashboardComponent()
        else -> {}
    }
}

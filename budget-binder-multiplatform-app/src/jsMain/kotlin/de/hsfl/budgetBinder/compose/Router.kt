package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import de.hsfl.budgetBinder.compose.dashboard.DashboardComponent
import de.hsfl.budgetBinder.compose.login.LoginComponent
import de.hsfl.budgetBinder.compose.register.RegisterComponent
import de.hsfl.budgetBinder.presentation.Screen

@Composable
fun Router(screenState: MutableState<Screen>) {
    when (screenState.value) {
        is Screen.Welcome -> {}
        is Screen.Register -> RegisterComponent(screenState = screenState)
        is Screen.Login -> LoginComponent(screenState = screenState)
        is Screen.Dashboard -> DashboardComponent(screenState = screenState)
    }
}
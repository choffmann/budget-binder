package de.hsfl.budgetBinder.presentation.viewmodel.welcome

import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow

class WelcomeViewModel(
    private val routerFlow: RouterFlow
) {
    fun onEvent(event: WelcomeEvent) {
        when (event) {
            is WelcomeEvent.OnNextScreen -> changeWelcomeScreen()
            is WelcomeEvent.OnSkip -> routerFlow.navigateTo(Screen.Welcome.GetStarted)
            is WelcomeEvent.OnLogin -> routerFlow.navigateTo(Screen.Login)
            is WelcomeEvent.OnRegister -> routerFlow.navigateTo(Screen.Register)
        }
    }

    private fun changeWelcomeScreen() {
        when (routerFlow.state.value) {
            is Screen.Welcome.Screen1 -> routerFlow.navigateTo(Screen.Welcome.Screen2)
            is Screen.Welcome.Screen2 -> routerFlow.navigateTo(Screen.Welcome.GetStarted)
        }
    }
}

package de.hsfl.budgetBinder.presentation.viewmodel.welcome

import de.hsfl.budgetBinder.domain.usecase.StoreIsFirstTimeUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WelcomeViewModel(
    private val routerFlow: RouterFlow,
    private val storeIsFirstTimeUseCase: StoreIsFirstTimeUseCase
) {

    private val _totalWelcomeScreen = MutableStateFlow(3)
    val totalWelcomeScreen: StateFlow<Int> = _totalWelcomeScreen

    private val _currentScreen = MutableStateFlow(0)
    val currentScreen: StateFlow<Int> = _currentScreen


    fun onEvent(event: WelcomeEvent) {
        when (event) {
            is WelcomeEvent.OnNextScreen -> changeWelcomeScreen()
            is WelcomeEvent.OnSkip -> onSkip()
            is WelcomeEvent.OnLogin -> onLogin()
            is WelcomeEvent.OnRegister -> onRegister()
        }
    }

    private fun onLogin() {
        storeIsFirstTimeUseCase()
        routerFlow.navigateTo(Screen.Login)
    }

    private fun onRegister() {
        storeIsFirstTimeUseCase()
        routerFlow.navigateTo(Screen.Register)
    }

    private fun onSkip() {
        _currentScreen.value = 3
        routerFlow.navigateTo(Screen.Welcome.GetStarted)
    }

    private fun changeWelcomeScreen() {
        _currentScreen.value = currentScreen.value + 1
        when (routerFlow.state.value) {
            is Screen.Welcome.Screen1 -> routerFlow.navigateTo(Screen.Welcome.Screen2)
            is Screen.Welcome.Screen2 -> routerFlow.navigateTo(Screen.Welcome.GetStarted)
        }
    }
}

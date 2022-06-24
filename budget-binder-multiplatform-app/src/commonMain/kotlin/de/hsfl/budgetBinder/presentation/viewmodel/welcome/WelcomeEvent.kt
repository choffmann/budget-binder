package de.hsfl.budgetBinder.presentation.viewmodel.welcome

import de.hsfl.budgetBinder.presentation.flow.RouterFlow

sealed class WelcomeEvent {
    object OnNextScreen: WelcomeEvent()
    object OnSkip: WelcomeEvent()
    object OnLogin: WelcomeEvent()
    object OnRegister: WelcomeEvent()
}

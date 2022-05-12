package de.hsfl.budgetBinder.presentation.component.user

interface UserView {
    fun onLogOut()

    sealed class Output {
        object LoggedOut : Output()
    }
}
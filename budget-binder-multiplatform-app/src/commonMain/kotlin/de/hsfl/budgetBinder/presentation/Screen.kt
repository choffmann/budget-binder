package de.hsfl.budgetBinder.presentation

sealed class Screen {
    object Welcome : Screen()
    object Login : Screen()
    object Register : Screen()
    object Home : Screen()
    object Settings : Screen()
    object Categories : Screen()
}

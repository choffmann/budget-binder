package de.hsfl.budgetBinder.compose

sealed class Screen {
    object Login : Screen()
    object User : Screen()
}

package de.hsfl.budgetBinder.compose

sealed class Screen {
    object Welcome : Screen()
    object Login : Screen()
    object Register : Screen()
    object User : Screen()
}

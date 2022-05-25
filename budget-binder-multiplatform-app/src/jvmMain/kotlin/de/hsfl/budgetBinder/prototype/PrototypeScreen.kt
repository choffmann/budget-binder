package de.hsfl.budgetBinder.prototype


// Define the Screens
sealed class PrototypeScreen {
    object Welcome : PrototypeScreen()
    object Home : PrototypeScreen()
    object Categories : PrototypeScreen()
    object Settings : PrototypeScreen()
    object Screen1 : PrototypeScreen()
    data class Screen2(val msg: String) : PrototypeScreen()
}

package de.hsfl.budgetBinder.prototype


// Define the Screens
sealed class PrototypeScreen {
    object Screen1 : PrototypeScreen()
    data class Screen2(val msg: String) : PrototypeScreen()
}

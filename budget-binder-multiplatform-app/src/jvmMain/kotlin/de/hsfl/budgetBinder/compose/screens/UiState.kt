package de.hsfl.budgetBinder.compose.screens



internal sealed class TextFieldState {
    object Nothing : TextFieldState()
    data class Error(val msg: String) : TextFieldState()
}
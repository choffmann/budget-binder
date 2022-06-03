package de.hsfl.budgetBinder.prototype.screens


sealed class UiState {
    object Nothing : UiState()
    object Loading : UiState()
    object Success : UiState()
    data class Error(val msg: String) : UiState()
}

sealed class TextFieldState {
    object Nothing : TextFieldState()
    data class Error(val msg: String) : TextFieldState()
}
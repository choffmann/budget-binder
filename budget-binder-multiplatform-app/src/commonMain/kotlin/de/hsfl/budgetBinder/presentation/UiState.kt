package de.hsfl.budgetBinder.presentation

sealed class UiState {
    object Empty : UiState()
    object Loading : UiState()
    data class Success<T>(val element: T) : UiState()
    data class Error(val error: String) : UiState()
}

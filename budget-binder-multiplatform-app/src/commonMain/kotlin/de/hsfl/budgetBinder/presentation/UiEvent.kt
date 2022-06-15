package de.hsfl.budgetBinder.presentation

sealed class UiEvent {
    // Show Loading State in Ui
    object ShowLoading : UiEvent()

    // Show Error, for example with a SnackBar
    data class ShowError(val msg: String) : UiEvent()
}

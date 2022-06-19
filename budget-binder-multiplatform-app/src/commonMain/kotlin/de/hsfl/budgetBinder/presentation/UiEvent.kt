package de.hsfl.budgetBinder.presentation

sealed class UiEvent {
    // Show Loading State in Ui
    object ShowLoading : UiEvent()

    // Show Error, for example with a SnackBar
    data class ShowError(val msg: String) : UiEvent()

    // Show Success
    data class ShowSuccess(val msg: String) : UiEvent()

    // Call on Success, which could reset the loading state
    object HideSuccess : UiEvent()
}

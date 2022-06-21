package de.hsfl.budgetBinder.presentation.viewmodel.entryViewModel

// View is not allowed to declare what should be done, only notify what has happened, names are assigned as such

sealed class EntryEvent {
    data class EnteredName(val value: String) : EntryEvent()
    data class EnteredAmount(val value: Float) : EntryEvent()
    data class EnteredRepeat(val value: Boolean) : EntryEvent()
    data class EnteredCategoryID(val value: Int) : EntryEvent()
    data class EnteredAmountSign(val value: Boolean) : EntryEvent()

    object OnCreateEntry : EntryEvent()
    object OnEditEntry : EntryEvent()
    object OnDeleteEntry : EntryEvent()
    object OnDeleteDialogConfirm : EntryEvent()
    object OnDeleteDialogDismiss : EntryEvent()

}

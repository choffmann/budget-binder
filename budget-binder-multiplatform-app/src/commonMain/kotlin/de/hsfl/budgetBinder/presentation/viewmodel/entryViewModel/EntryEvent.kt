package de.hsfl.budgetBinder.presentation.viewmodel.entryViewModel

// View is not allowed to declare what should be done, only notify what has happened, names are assigned as such

sealed class EntryEvent {
    data class EnteredName(val value: String) : EntryEvent()

    //If the amount will be added or subtracted from the current spent sum
    data class EnteredAmountInputType(val value: Boolean) : EntryEvent()
    data class EnteredAmount(val value: Float) : EntryEvent()
    data class EnteredRepeatState(val value: Boolean) : EntryEvent()
    data class EnteredCategoryID(val value: Int) : EntryEvent()
    object OnCreateEntry : EntryEvent()
    object OnEditEntry : EntryEvent()
    object OnDeleteEntry : EntryEvent()
    object OnDeleteDialogConfirm : EntryEvent()
    object OnDeleteDialogDismiss : EntryEvent()

}

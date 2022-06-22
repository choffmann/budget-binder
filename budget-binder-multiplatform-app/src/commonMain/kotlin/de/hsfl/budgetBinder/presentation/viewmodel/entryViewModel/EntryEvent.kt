package de.hsfl.budgetBinder.presentation.viewmodel.entryViewModel

// View is not allowed to declare what should be done, only notify what has happened, names are assigned as such

sealed class EntryEvent {
    //User-made Data Input
    data class EnteredName(val value: String) : EntryEvent()
    data class EnteredAmount(val value: Float) : EntryEvent()
    object EnteredRepeat : EntryEvent()
    data class EnteredCategoryID(val value: Int?) : EntryEvent()
    object EnteredAmountSign : EntryEvent()

    //Action
    object OnCreateEntry : EntryEvent()
    object OnEditEntry : EntryEvent()
    object OnDeleteEntry : EntryEvent()
    object OnDeleteDialogConfirm : EntryEvent()
    object OnDeleteDialogDismiss : EntryEvent()

}

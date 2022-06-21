package de.hsfl.budgetBinder.presentation.viewmodel.entryViewModel

import de.hsfl.budgetBinder.common.Entry

data class EntryState(
    val selectedEntry: Entry = Entry(0,"",0f,false,null)
)

package de.hsfl.budgetBinder.presentation.viewmodel.settings

import de.hsfl.budgetBinder.presentation.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SettingsSharedFlow {
    val mutableEventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = mutableEventFlow.asSharedFlow()
}

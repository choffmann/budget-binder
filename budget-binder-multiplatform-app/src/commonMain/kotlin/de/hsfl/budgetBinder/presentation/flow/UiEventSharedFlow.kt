package de.hsfl.budgetBinder.presentation.flow

import de.hsfl.budgetBinder.presentation.event.UiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object UiEventSharedFlow {
    val mutableEventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = mutableEventFlow.asSharedFlow()
}

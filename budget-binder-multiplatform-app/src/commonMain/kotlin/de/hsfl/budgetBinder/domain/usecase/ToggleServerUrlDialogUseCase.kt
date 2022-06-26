package de.hsfl.budgetBinder.domain.usecase

import kotlinx.coroutines.flow.*

class ToggleServerUrlDialogUseCase {
    private var dialog = false
    private val _dialogState = MutableSharedFlow<Boolean>(replay = 0)
    val dialogState: SharedFlow<Boolean> = _dialogState


    suspend operator fun invoke() {
        dialog = !dialog
        _dialogState.emit(dialog)
    }
}

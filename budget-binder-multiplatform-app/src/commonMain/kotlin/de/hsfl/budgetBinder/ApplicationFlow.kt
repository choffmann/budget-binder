package de.hsfl.budgetBinder

import de.hsfl.budgetBinder.client.Client
import de.hsfl.budgetBinder.common.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ApplicationFlow(private val client: Client, private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())) {
    private val _uiState = MutableStateFlow<UIState>(UIState.Empty)
    val uiState: StateFlow<UIState> = _uiState

    init {
        scope.launch {
            _update()
        }
    }

    fun update() {
        scope.launch {
            _update()
        }
    }

    private suspend fun _update() {
        try {
            val users = client.getUsers()
            _uiState.value = UIState.Success(users)
        } catch (e: Throwable) {
            _uiState.value = UIState.Error(e)
        }
    }
}

sealed class UIState {
    object Empty : UIState()
    data class Success(val users: List<User>) : UIState()
    data class Error(val error: Throwable): UIState()
}
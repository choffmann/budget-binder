package de.hsfl.budgetBinder

import de.hsfl.budgetBinder.client.Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ApplicationFlow(
    private val client: Client,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
) {
    private val _uiState = MutableStateFlow<UIState>(UIState.Empty)
    val uiState = _uiState.asStateFlow()

    init {
        scope.launch {
            getUserInit()
        }
    }

    fun update() {
        scope.launch {
            getUserUpdate()
            println(_uiState.value)
        }
    }


    private suspend fun getUserInit() {
        try {
            client.authorize("root@admin.com", "changeme")
            val userdata = client.getMyUserInfo()
            _uiState.value = UIState.User(userdata.data)
        } catch (e: Throwable) {
            _uiState.value = UIState.Error(e)
            e.printStackTrace()
        }
    }

    private suspend fun getUserUpdate() {
        try {
            //client.authorize("root@admin.com", "changeme")
            val userdata = client.getMyUserInfo()
            _uiState.value = UIState.User(userdata.data)
        } catch (e: Throwable) {
            _uiState.value = UIState.Error(e)
            e.printStackTrace()
        }
    }
}

sealed class UIState {
    object Empty : UIState()
    data class User(val user: de.hsfl.budgetBinder.common.User) : UIState()
    data class Path(val data: String) : UIState()
    data class Error(val error: Throwable) : UIState()
}
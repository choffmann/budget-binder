package de.hsfl.budgetBinder.presentation.flow

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.domain.usecase.DataFlowUseCases
import de.hsfl.budgetBinder.domain.usecase.storage.StoreUserStateUseCase
import io.ktor.http.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

class DataFlow(
    private val dataFlowUseCases: DataFlowUseCases
) {
    // User data information from backend
    private val _userState = MutableStateFlow(User(0, "", "", ""))
    val userState: StateFlow<User> = _userState

    suspend fun storeUserState(user: User) {
        dataFlowUseCases.storeUserStateUseCase(user).collect {
            _userState.value = it
        }
    }

    // Server Url
    private val _serverUrlState = MutableStateFlow(Url("http://localhost:8080"))
    val serverUrlState: StateFlow<Url> = _serverUrlState

    suspend fun storeServerUrl(serverUrl: Url) {
        dataFlowUseCases.storeServerUrlUseCase(serverUrl).collect {
            _serverUrlState.value = it
        }
    }

    // Dark Mode
    private val _darkModeState = MutableStateFlow(false)
    val darkModeState: StateFlow<Boolean> = _darkModeState

    suspend fun toggleDarkMode() {
        dataFlowUseCases.storeDarkModeUseCase(!darkModeState.value).collect {
            _darkModeState.value = it
        }
    }
}

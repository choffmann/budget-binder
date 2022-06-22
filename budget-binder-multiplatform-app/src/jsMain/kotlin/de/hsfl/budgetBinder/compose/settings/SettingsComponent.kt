package de.hsfl.budgetBinder.compose.settings

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsViewModel
import di
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun SettingsComponent() {
    val viewModel: SettingsViewModel by di.instance()
    val dataFlow: DataFlow by di.instance()
    val routerFlow: RouterFlow by di.instance()
    val userState = dataFlow.userState.collectAsState()
    val screenState = routerFlow.state.collectAsState()
    val loadingState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is UiEvent.ShowLoading -> loadingState.value = true
                else -> loadingState.value = false
            }
        }
    }
    when (screenState.value) {
        is Screen.Settings.Menu -> SettingsView()
        is Screen.Settings.User -> SettingsChangeUserDataView()
        else -> {}
    }
}
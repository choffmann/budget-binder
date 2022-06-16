package de.hsfl.budgetBinder.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.compose.icon.AvatarImage
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun SettingsView() {
    val viewModel: SettingsViewModel by di.instance()
    val dataFlow: DataFlow by di.instance()
    val routerFlow: RouterFlow by di.instance()
    val userState = dataFlow.userState.collectAsState()
    val screenState = routerFlow.state.collectAsState()
    val loadingState = remember { mutableStateOf(false) }


    // Toggle LoadingState
    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest {
            when (it) {
                is UiEvent.ShowLoading -> loadingState.value = true
                else -> loadingState.value = false
            }
        }
    }

    if (loadingState.value) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        AvatarImage(modifier = Modifier.size(128.dp).padding(16.dp))
        Text(text = "${userState.value.firstName} ${userState.value.name}", style = MaterialTheme.typography.h5)
        Text(text = userState.value.email, style = MaterialTheme.typography.subtitle1)
        Spacer(modifier = Modifier.height(8.dp))
        when (screenState.value) {
            is Screen.Settings.Menu -> SettingsMenuView(modifier = Modifier.fillMaxWidth())
            is Screen.Settings.User -> SettingsEditUserView(
                modifier = Modifier.padding(16.dp),
                isLoading = loadingState.value
            )
            is Screen.Settings.Server -> SettingsServerUrlView(modifier = Modifier.fillMaxWidth())
        }
    }
}

package de.hsfl.budgetBinder.compose.settings

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsViewModel
import di
import org.kodein.di.instance

@Composable
fun SettingsComponent(screenState: MutableState<Screen>) {
    /*val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val changeMyUserUseCase: ChangeMyUserUseCase by di.instance()
    val deleteMyUserUseCase: DeleteMyUserUseCase by di.instance()
    val settingsViewModel = SettingsViewModel(changeMyUserUseCase, deleteMyUserUseCase, scope)
    val viewState = settingsViewModel.state.collectAsState(scope)*/
    val scope = rememberCoroutineScope()
    val settingsViewModel: SettingsViewModel by di.instance()
    val viewState = settingsViewModel.state.collectAsState(scope)


    when (screenState.value) {
        Screen._Settings -> SettingsView(
            state = viewState,
            onChangeToDashboard = { screenState.value = Screen.Dashboard },
            onChangeToSettings = { screenState.value = Screen._Settings },
            onChangeToCategory = { screenState.value = Screen.CategorySummary },
            onDeleteButtonPressed = { settingsViewModel.deleteMyUser(); screenState.value = Screen.Login },
            onChangeButtonPressed = { screenState.value = Screen.SettingsChangeUserData }
        )
        Screen.SettingsChangeUserData -> SettingsChangeUserDataView(
            state = viewState,
            onChangeDataButtonPressed = { firstName, lastName, password ->
                settingsViewModel.changeMyUser(User.Patch(firstName, lastName, password)); screenState.value =
                Screen._Settings
            },
            onChangeToDashboard = { screenState.value = Screen.Dashboard },
            onChangeToSettings = { screenState.value = Screen._Settings },
            onChangeToCategory = { screenState.value = Screen.CategorySummary },
        )
        else -> {}
    }
}
package de.hsfl.budgetBinder.compose.settings

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.compose.settings.SettingsChangeUserDataView
import de.hsfl.budgetBinder.domain.usecase.ChangeMyUserUseCase
import de.hsfl.budgetBinder.domain.usecase.DeleteMyUserUseCase
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun SettingsComponent(screenState: MutableState<Screen>) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val di = localDI()
    val changeMyUserUseCase: ChangeMyUserUseCase by di.instance()
    val deleteMyUserUseCase: DeleteMyUserUseCase by di.instance()
    val settingsViewModel = SettingsViewModel(changeMyUserUseCase, deleteMyUserUseCase, scope)
    val viewState = settingsViewModel.state.collectAsState(scope)

    when (screenState.value) {
        Screen.Settings -> SettingsView(
            state = viewState,
            onChangeToDashboard = { screenState.value = Screen.Dashboard },
            onChangeToSettings = { screenState.value = Screen.Settings },
            onChangeToCategory = { screenState.value = Screen.CategorySummary },
            onDeleteButtonPressed = { settingsViewModel.deleteMyUser(); screenState.value = Screen.Login },
            onChangeButtonPressed = { screenState.value = Screen.SettingsChangeUserData }
        )
        Screen.SettingsChangeUserData -> SettingsChangeUserDataView(
            state = viewState,
            onChangeDataButtonPressed = { firstName, lastName, password ->
                settingsViewModel.changeMyUser(User.In(firstName, lastName, password)); screenState.value =
                Screen.Settings
            },
            onChangeToDashboard = { screenState.value = Screen.Dashboard },
            onChangeToSettings = { screenState.value = Screen.Settings },
            onChangeToCategory = { screenState.value = Screen.CategorySummary },
        )
        else -> {}
    }
}
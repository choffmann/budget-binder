package de.hsfl.budgetBinder.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.data.repository.AuthRepositoryImplementation
import de.hsfl.budgetBinder.data.repository.UserRepositoryImplementation
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import de.hsfl.budgetBinder.presentation.UserState
import de.hsfl.budgetBinder.presentation.UserViewModel

@Composable
fun UserView() {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val client = Client()
    val userViewModel = UserViewModel(
        // TODO: Optimize with KODEIN DI
        LoginUseCase(AuthRepositoryImplementation(client)),
        UserUseCase(UserRepositoryImplementation(client)),
        scope
    )
    val uiState by userViewModel.state.collectAsState(scope)
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize())
        Column {

            when (uiState) {
                is UserState.Success -> {
                    Text((uiState as UserState.Success).user.toString())
                }
                is UserState.Error -> {
                    Text((uiState as UserState.Error).error)
                }
                is UserState.Loading -> {
                    CircularProgressIndicator()
                }
            }
            Button(onClick = { userViewModel.getMyUser() }) {
                Text("Update")
            }
        }
    }
}

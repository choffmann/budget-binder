package de.hsfl.budgetBinder.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
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
import de.hsfl.budgetBinder.presentation.UserViewModel

@Composable
fun UserView() {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val userViewModel = UserViewModel(
        // TODO: Optimize with KODEIN DI
        LoginUseCase(
            AuthRepositoryImplementation(Client())
        ),
        UserUseCase(
            UserRepositoryImplementation(Client())
        ),
        scope
    )
    val uiState = userViewModel.state.value
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize())
        Column {
            uiState.user?.let { user ->
                Text(user.toString())
            }
            if(uiState.error.isNotBlank()) {
                Text(uiState.error)
            }
            if(uiState.isLoading) {
                Text("Loading...")
            }
            Button(onClick = { userViewModel.getMyUser() }) {
                Text("Update")
            }
        }
    }
}

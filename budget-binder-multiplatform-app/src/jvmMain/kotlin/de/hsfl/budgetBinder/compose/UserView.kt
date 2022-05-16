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
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.UserViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.instance

@Composable
fun UserView() {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

    val di = localDI()
    val userUseCase: UserUseCase by di.instance()
    val viewModel = UserViewModel(userUseCase,scope)

    val uiState by viewModel.state.collectAsState(scope)
    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize())
        Column {
            when (uiState) {
                is UiState.Success<*> -> {
                    Text((uiState as UiState.Success<*>).element.toString())
                }
                is UiState.Error -> {
                    Text((uiState as UiState.Error).error)
                }
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
            }
            Button(onClick = { viewModel.getMyUser() }) {
                Text("Update")
            }
        }
    }
}

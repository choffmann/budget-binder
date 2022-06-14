package de.hsfl.budgetBinder.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.viewmodel.DashboardViewModel
import org.kodein.di.instance

@Composable
fun DashboardComponent() {
    val scope = rememberCoroutineScope()
    val dataFlow: DataFlow by di.instance()
    val userState = dataFlow.userState.collectAsState(scope.coroutineContext)
    val viewModel: DashboardViewModel by di.instance()
    val logoutState = viewModel.state.collectAsState(scope.coroutineContext)

    Column {
        Text(userState.value.toString())
        Row {
            Button(onClick = { viewModel.logOut(true) }) {
                Text("Logout")
            }
            Button(onClick = { viewModel.getMyUser() }) {
                Text("Update")
            }
        }
    }
}

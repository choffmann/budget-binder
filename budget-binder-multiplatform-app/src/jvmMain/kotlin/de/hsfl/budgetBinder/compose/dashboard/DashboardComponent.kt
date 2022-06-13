package de.hsfl.budgetBinder.compose.dashboard

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import de.hsfl.budgetBinder.compose.di
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import org.kodein.di.instance

@Composable
fun DashboardComponent() {
    val scope = rememberCoroutineScope()
    val dataFlow: DataFlow by di.instance()
    val userState = dataFlow.userState.collectAsState(scope.coroutineContext)

    Text(userState.value.toString())
}

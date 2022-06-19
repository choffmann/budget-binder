package de.hsfl.budgetBinder

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.compose.icon.*
import de.hsfl.budgetBinder.presentation.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.viewmodel.navdrawer.NavDrawerEvent
import de.hsfl.budgetBinder.presentation.viewmodel.navdrawer.NavDrawerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.instance

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NavDrawer(
    drawerState: DrawerState,
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewModel: NavDrawerViewModel by di.instance()
    ModalDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        content = content,
        drawerContent = {
            UserData()
            ListItem(
                icon = { DashboardIcon() },
                text = { Text("Dashboard") },
                modifier = Modifier.clickable(onClick = {
                    scope.launch {
                        drawerState.close()
                        viewModel.onEvent(NavDrawerEvent.OnDashboard)
                    }
                })
            )
            ListItem(
                icon = { CategoryIcon() },
                text = { Text("Categories") },
                modifier = Modifier.clickable(onClick = {
                    scope.launch {
                        drawerState.close()
                        viewModel.onEvent(NavDrawerEvent.OnCategory)
                    }
                })
            )
            ListItem(
                icon = { SettingsIcon() },
                text = { Text("Settings") },
                modifier = Modifier.clickable(onClick = {
                    scope.launch {
                        drawerState.close()
                        viewModel.onEvent(NavDrawerEvent.OnSettings)
                    }
                })
            )
            ListItem(
                icon = { LogoutIcon() },
                text = { Text("Logout") },
                modifier = Modifier.clickable(onClick = {
                    scope.launch {
                        drawerState.close()
                        viewModel.onEvent(NavDrawerEvent.OnLogout)
                    }
                })
            )
        }
    )
}

@Composable
fun UserData() {
    val dataFlow: DataFlow by di.instance()
    val userData = dataFlow.userState.collectAsState()

    Column {
        Row(modifier = Modifier.padding(8.dp),verticalAlignment = Alignment.CenterVertically) {
            AvatarImage(modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(verticalArrangement = Arrangement.Center) {
                Text(text = "${userData.value.firstName} ${userData.value.name}", style = MaterialTheme.typography.h5)
                Text(text = userData.value.email, style = MaterialTheme.typography.body1)
            }
        }
        Divider()
    }
}

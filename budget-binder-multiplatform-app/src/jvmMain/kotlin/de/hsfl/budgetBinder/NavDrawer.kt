package de.hsfl.budgetBinder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.compose.icon.*
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.viewmodel.auth.AuthViewModel
import de.hsfl.budgetBinder.presentation.viewmodel.navdrawer.NavDrawerEvent
import de.hsfl.budgetBinder.presentation.viewmodel.navdrawer.NavDrawerViewModel
import kotlinx.coroutines.launch
import org.kodein.di.instance

@Composable
fun BudgetBinderTopBar(
    text: String = "Budget Binder",
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(title = { Text("Budget Binder") }, navigationIcon = navigationIcon, actions = actions)
}

@Composable
fun TopBarMenuIcon(drawerState: DrawerState) {
    val scope = rememberCoroutineScope()
    IconButton(onClick = {
        scope.launch {
            if (drawerState.isOpen) drawerState.close()
            else drawerState.open()
        }
    }) {
        Icon(Icons.Default.Menu, contentDescription = null)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BudgetBinderAuthNavDrawer(drawerState: DrawerState, content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    ModalDrawer(drawerState = drawerState, gesturesEnabled = true, content = content, drawerContent = {
        ListItem(icon = { AppIcon(modifier = Modifier.size(64.dp)) }, text = { Text("Budget Binder") })
        Divider()
        ListItem(icon = { ServerIcon() }, text = { Text("Change Server URL") }, modifier = Modifier.clickable {
            scope.launch {
                drawerState.close()
            }
        })
    })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BudgetBinderNavDrawer(
    drawerState: DrawerState, content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val viewModel: NavDrawerViewModel by di.instance()
    ModalDrawer(drawerState = drawerState, gesturesEnabled = true, content = content, drawerContent = {
        UserData()
        ListItem(icon = { DashboardIcon() }, text = { Text("Dashboard") }, modifier = Modifier.clickable(onClick = {
            scope.launch {
                drawerState.close()
                viewModel.onEvent(NavDrawerEvent.OnDashboard)
            }
        })
        )
        ListItem(icon = { CategoryIcon() }, text = { Text("Categories") }, modifier = Modifier.clickable(onClick = {
            scope.launch {
                drawerState.close()
                viewModel.onEvent(NavDrawerEvent.OnCategory)
            }
        })
        )
        ListItem(icon = { SettingsIcon() }, text = { Text("Settings") }, modifier = Modifier.clickable(onClick = {
            scope.launch {
                drawerState.close()
                viewModel.onEvent(NavDrawerEvent.OnSettings)
            }
        })
        )
        ListItem(icon = { LogoutIcon() }, text = { Text("Logout") }, modifier = Modifier.clickable(onClick = {
            scope.launch {
                drawerState.close()
                viewModel.onEvent(NavDrawerEvent.OnLogout)
            }
        })
        )
    })
}

@Composable
fun UserData() {
    val dataFlow: DataFlow by di.instance()
    val userData = dataFlow.userState.collectAsState()

    Column {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
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

package de.hsfl.budgetBinder

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.di.kodein
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.kodein.di.compose.withDI
import org.kodein.di.instance

val di = kodein(ktorEngine = CIO.create())

@Composable
fun App() = withDI(di) {
    val scope = rememberCoroutineScope()
    val dataFlow: DataFlow by di.instance()
    val uiEventFlow: UiEventSharedFlow by di.instance()
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState()
    val darkTheme = dataFlow.darkModeState.collectAsState(scope.coroutineContext)
    val scaffoldState = rememberScaffoldState()
    val loadingState = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        uiEventFlow.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowLoading -> loadingState.value = true
                is UiEvent.ShowError -> {
                    loadingState.value = false
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.msg,
                        actionLabel = "Dismiss"
                    )
                }
                is UiEvent.ShowSuccess -> {
                    loadingState.value = false
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.msg,
                        actionLabel = "Dismiss"
                    )
                }
                else -> {}
            }
        }
    }

    MaterialTheme(
        colors = if (darkTheme.value) darkColors() else lightColors()
    ) {
        if (loadingState.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Scaffold(scaffoldState = scaffoldState,
        topBar = { BudgetBinderTopBar(navigationIcon = { TopBarMenuIcon(drawerState = scaffoldState.drawerState) }) }) {
            when (screenState.value) {
                is Screen.Login, is Screen.Register -> {
                    BudgetBinderAuthNavDrawer(scaffoldState.drawerState) {
                        Router()
                    }
                }
                else -> {
                    BudgetBinderNavDrawer(scaffoldState.drawerState) {
                        Router()
                    }
                }
            }
        }
    }
}

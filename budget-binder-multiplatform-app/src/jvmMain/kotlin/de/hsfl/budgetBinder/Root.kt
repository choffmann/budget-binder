package de.hsfl.budgetBinder

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.di.kodein
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.UiEvent
import de.hsfl.budgetBinder.presentation.flow.DataFlow
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.flow.UiEventSharedFlow
import de.hsfl.budgetBinder.presentation.viewmodel.RootEvent
import de.hsfl.budgetBinder.presentation.viewmodel.RootViewModel
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.compose.withDI
import org.kodein.di.instance

val di = kodein(ktorEngine = CIO.create())

@Composable
fun App() = withDI(di) {
    val viewModel: RootViewModel by di.instance()
    val uiEventFlow: UiEventSharedFlow by di.instance()
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState()
    val darkTheme = viewModel.darkModeState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val loadingState = remember { mutableStateOf(false) }
    val showTopBarMenuIcon = remember { mutableStateOf(false) }
    val initDarkMode = isSystemInDarkTheme()

    LaunchedEffect(key1 = true) {
        viewModel.onEvent(RootEvent.InitDarkMode(initDarkMode))
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
            topBar = { BudgetBinderTopBar(navigationIcon = { if (showTopBarMenuIcon.value) TopBarMenuIcon(drawerState = scaffoldState.drawerState) }) }) {
            when (screenState.value) {
                is Screen.Welcome -> {
                    showTopBarMenuIcon.value = false
                    Router()
                }
                is Screen.Login, is Screen.Register -> {
                    showTopBarMenuIcon.value = true
                    BudgetBinderAuthNavDrawer(scaffoldState.drawerState) {
                        Router()
                    }
                }
                else -> {
                    showTopBarMenuIcon.value = true
                    BudgetBinderNavDrawer(scaffoldState.drawerState) {
                        Router()
                    }
                }
            }
        }
    }
}

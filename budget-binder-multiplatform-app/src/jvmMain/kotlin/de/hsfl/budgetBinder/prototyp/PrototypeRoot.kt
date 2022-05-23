package de.hsfl.budgetBinder.prototyp

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

// Root Component to handle View's
@Composable
fun Prototype() {
    val screenState = remember { mutableStateOf<PrototypeScreen>(PrototypeScreen.Screen1) }
    val scaffoldState = rememberScaffoldState()
    val darkTheme = remember { mutableStateOf(false) }
    MaterialTheme(
        colors = if (darkTheme.value) darkColors() else lightColors()
    ) {

        // Scaffold to handle AppBar, Floating Buttons, Snackbar and Co.
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { PrototypeAppBar() },
            snackbarHost = {},
            floatingActionButton = {},
            floatingActionButtonPosition = FabPosition.End,
            isFloatingActionButtonDocked = false,
        ) {
            Router(screenState)
        }
    }

}
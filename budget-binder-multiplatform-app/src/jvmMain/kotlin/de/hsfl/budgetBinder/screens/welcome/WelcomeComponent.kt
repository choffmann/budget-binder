package de.hsfl.budgetBinder.screens.welcome

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.compose.icon.GetStartedImage
import de.hsfl.budgetBinder.compose.icon.SavingsImage
import de.hsfl.budgetBinder.compose.icon.WelcomeImage
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.Screen
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.welcome.WelcomeEvent
import de.hsfl.budgetBinder.presentation.viewmodel.welcome.WelcomeViewModel
import org.kodein.di.instance

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeComponent() {
    val viewModel: WelcomeViewModel by di.instance()
    val routerFlow: RouterFlow by di.instance()
    val screenState = routerFlow.state.collectAsState()
    val totalScreens = viewModel.totalWelcomeScreen.collectAsState()
    val currentScreen = viewModel.currentScreen.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onEvent(WelcomeEvent.Lifecycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(WelcomeEvent.Lifecycle(LifecycleEvent.OnDispose))
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(targetState = screenState.value, transitionSpec = {
            slideInHorizontally { fullWidth -> fullWidth } + fadeIn() with slideOutHorizontally { fullWidth -> -fullWidth }
        }) { state ->
            when (state) {
                is Screen.Welcome.Screen1 -> WelcomeScreen1View()
                is Screen.Welcome.Screen2 -> WelcomeScreen2View()
                is Screen.Welcome.GetStarted -> WelcomeGetStartedView()
            }
        }
        when (screenState.value) {
            is Screen.Welcome.Screen1, is Screen.Welcome.Screen2 -> BottomButtons(totalScreens = totalScreens.value,
                currentScreen = currentScreen.value,
                onNext = { viewModel.onEvent(WelcomeEvent.OnNextScreen) },
                onSkip = { viewModel.onEvent(WelcomeEvent.OnSkip) })
            is Screen.Welcome.GetStarted -> GetStartedButton(onLogin = { viewModel.onEvent(WelcomeEvent.OnLogin) },
                onRegister = { viewModel.onEvent(WelcomeEvent.OnRegister) })
        }
    }
}

@Composable
fun WelcomeScreen1View(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        WelcomeImage(modifier = Modifier.size(254.dp).padding(top = 8.dp, start = 16.dp, end = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        WelcomeText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            title = "Welcome to Budget Binder",
            subtitle = "You have made a great choice! Use Budget Binder to manage your money, easily. Simply track your monthly spending. Write down your recurring income and expenses and set a budget, you would like to save. The App is available on all of your favorite devices."
        )
    }
}

@Composable
fun WelcomeScreen2View(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        SavingsImage(modifier = Modifier.size(254.dp).padding(top = 8.dp, start = 16.dp, end = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        WelcomeText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            title = "How you can save your money",
            subtitle = "With budget Binder, you can keep your spending organized. Create categories with a budget, icons and colors. You can make an entry with your spending or income and organized these in your categories"
        )
    }
}

@Composable
fun WelcomeGetStartedView(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        GetStartedImage(modifier = Modifier.size(254.dp).padding(top = 8.dp, start = 16.dp, end = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        WelcomeText(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            title = "So, let's get started",
            subtitle = "Register an account and create your first category or log in with a previous made account and continue where you left off!"
        )
    }
}

@Composable
private fun WelcomeText(modifier: Modifier = Modifier, title: String, subtitle: String) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = subtitle, style = MaterialTheme.typography.body1, textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BottomButtons(totalScreens: Int, currentScreen: Int, onNext: () -> Unit, onSkip: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp),
            //verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(modifier = Modifier, onClick = { onSkip() }) {
                Text("Skip")
            }

            Stepper(
                modifier = Modifier,
                total = totalScreens,
                current = currentScreen,
            )

            Button(modifier = Modifier, onClick = { onNext() }) {
                Text("Next")
            }
        }
    }
}

@Composable
private fun GetStartedButton(onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedButton(onClick = onRegister) {
            Text(modifier = Modifier.padding(start = 16.dp, end = 16.dp), text = "Register")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onLogin) {
            Text(modifier = Modifier.padding(start = 16.dp, end = 16.dp), text = "Login")
        }
    }
}

@Composable
fun Stepper(modifier: Modifier, total: Int, current: Int) {
    Row(modifier = modifier) {
        for (i in (0 until total)) {
            if (i == current) {
                Box(
                    modifier = Modifier.size(16.dp).padding(4.dp).clip(CircleShape)
                        .background(MaterialTheme.colors.primary)
                )
            } else {
                Box(
                    modifier = Modifier.size(16.dp).padding(4.dp).clip(CircleShape)
                        .background(MaterialTheme.colors.secondary)
                )
            }
        }
    }

}

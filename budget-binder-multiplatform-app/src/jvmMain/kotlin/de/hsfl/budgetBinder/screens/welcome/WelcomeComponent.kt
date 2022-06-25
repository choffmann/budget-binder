package de.hsfl.budgetBinder.screens.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import de.hsfl.budgetBinder.presentation.flow.RouterFlow
import de.hsfl.budgetBinder.presentation.viewmodel.welcome.WelcomeEvent
import de.hsfl.budgetBinder.presentation.viewmodel.welcome.WelcomeViewModel
import org.kodein.di.instance

@Composable
fun WelcomeComponent() {
    val viewModel: WelcomeViewModel by di.instance()
    val routerFlow: RouterFlow by di.instance()
    val totalScreens = viewModel.totalWelcomeScreen.collectAsState()
    val currentScreen = viewModel.currentScreen.collectAsState()
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        when (routerFlow.state.value) {
            is Screen.Welcome.Screen1 -> WelcomeScreen1View()
            is Screen.Welcome.Screen2 -> WelcomeScreen2View()
            is Screen.Welcome.GetStarted -> WelcomeGetStartedView()
        }
        BottomButtons(totalScreens = totalScreens.value,
            currentScreen = currentScreen.value,
            onNext = { viewModel.onEvent(WelcomeEvent.OnNextScreen) },
            onSkip = { viewModel.onEvent(WelcomeEvent.OnSkip) })
    }
}

@Composable
fun WelcomeScreen1View(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        WelcomeImage(modifier = Modifier.size(254.dp))
        WelcomeText(
            modifier = Modifier.padding(16.dp),
            title = "Welcome to Budget Binder",
            subtitle = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"
        )
    }
}

@Composable
fun WelcomeScreen2View(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SavingsImage(modifier = Modifier.size(254.dp))
        WelcomeText(
            modifier = Modifier.padding(16.dp),
            title = "How you can save your money",
            subtitle = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"
        )
    }
}

@Composable
fun WelcomeGetStartedView(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        GetStartedImage(modifier = Modifier.size(254.dp))
        WelcomeText(
            modifier = Modifier.padding(16.dp),
            title = "So, let's get started",
            subtitle = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"
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
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(modifier = Modifier.weight(1F), onClick = { onSkip() }) {
                Text("SKIP")
            }

            Stepper(
                modifier = Modifier.weight(1F),
                total = totalScreens,
                current = currentScreen,
            )

            Button(modifier = Modifier.weight(1F), onClick = { onNext() }) {
                Text("NEXT")
            }
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

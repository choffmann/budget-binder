package de.hsfl.budgetBinder.prototype.screens.welcome

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.PrototypeScreen
import de.hsfl.budgetBinder.prototype.StateManager.screenState


private val welcomeScreenState: MutableState<WelcomeScreen> = mutableStateOf(WelcomeScreen.Screen1())

@Composable
fun WelcomeComponent() {
    WelcomeView()
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun WelcomeView() {
    Column {
        AnimatedContent(targetState = welcomeScreenState.value, transitionSpec = {
            if (targetState.current > initialState.current) {
                // If welcomeScreenState ID is larger than previous ID,
                // slide in from right and slide out to left
                slideInHorizontally { fullWidth -> fullWidth } + fadeIn() with
                        slideOutHorizontally { fullWidth -> -fullWidth }
            } else {
                // If welcomeScreenState ID is smaller than previous ID,
                // slide in from left and slide out to right
                slideInHorizontally { fullWidth -> -fullWidth } + fadeIn() with
                        slideOutHorizontally { fullWidth -> fullWidth }
            }.using(SizeTransform(clip = false))
        }) { state ->
            when (state) {
                is WelcomeScreen.Screen1 -> Screen1()
                is WelcomeScreen.Screen2 -> Screen2()
                is WelcomeScreen.GetStarted -> Screen3()
            }
        }

        BottomButtons(onNext = {
            when (welcomeScreenState.value) {
                is WelcomeScreen.Screen1 -> {
                    welcomeScreenState.value = WelcomeScreen.Screen2()
                }
                is WelcomeScreen.Screen2 -> {
                    welcomeScreenState.value = WelcomeScreen.GetStarted()
                }
                else -> {}
            }
        }, onSkip = { welcomeScreenState.value = WelcomeScreen.GetStarted() })
    }
}

@Composable
private fun Screen1() {
    Column {
        ImageWelcomeScreen1(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
        WelcomeText(
            title = "Welcome to Budget Binder",
            subtitle = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"
        )
    }
}

@Composable
private fun Screen2() {
    Column {
        ImageWelcomeScreen2(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
        WelcomeText(
            title = "How you can save your money",
            subtitle = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"
        )
    }
}

@Composable
private fun Screen3() {
    Column {
        ImageWelcomeScreen3(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
        WelcomeText(
            title = "So, let's get started",
            subtitle = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"
        )
        GetStartedButton(onLogin = { screenState.value = PrototypeScreen.Login }, onRegister = {})
    }
}


@Composable
private fun WelcomeText(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = subtitle, style = MaterialTheme.typography.body1, textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BottomButtons(onNext: () -> Unit, onSkip: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.align(Alignment.BottomCenter), verticalAlignment = Alignment.Bottom
        ) {
            OutlinedButton(modifier = Modifier.padding(16.dp).wrapContentWidth(Alignment.Start).weight(1F),
                onClick = { onSkip() }) {
                Text("SKIP")
            }

            Stepper(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center).weight(1F),
                total = WelcomeScreen::class.nestedClasses.size,
                current = welcomeScreenState.value.current,
                onClick = { /* Eventuell Screen wechsel bei Click auf Stepper, Position wird hier mitgesendet */ })

            Button(modifier = Modifier.padding(16.dp).wrapContentWidth(Alignment.End).weight(1F),
                onClick = { onNext() }) {
                Text("NEXT")
            }
        }
    }
}

@Composable
private fun GetStartedButton(onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedButton(modifier = Modifier.padding(top = 16.dp), onClick = onRegister) {
            Text(modifier = Modifier.padding(start = 16.dp, end = 16.dp), text = "Sign Up")
        }
        Button(modifier = Modifier.padding(top = 8.dp), onClick = onLogin) {
            Text(modifier = Modifier.padding(start = 16.dp, end = 16.dp), text = "Sign In")
        }
    }
}

@Composable
fun Stepper(modifier: Modifier, total: Int, current: Int, onClick: (Int) -> Unit) {
    Box(modifier = modifier) {
        Row {
            for (i in (0 until total)) {
                if (i == current) {
                    Box(
                        modifier = Modifier.size(16.dp).padding(4.dp).clip(CircleShape)
                            .background(MaterialTheme.colors.primary).clickable(onClick = { onClick(i) })
                    )
                } else {
                    Box(
                        modifier = Modifier.size(16.dp).padding(4.dp).clip(CircleShape)
                            .background(MaterialTheme.colors.secondary).clickable(onClick = { onClick(i) })
                    )
                }
            }
        }
    }
}

@Composable
expect fun ImageWelcomeScreen1(modifier: Modifier = Modifier)

@Composable
expect fun ImageWelcomeScreen2(modifier: Modifier = Modifier)

@Composable
expect fun ImageWelcomeScreen3(modifier: Modifier = Modifier)

sealed class WelcomeScreen(val current: Int = 0) {
    data class Screen1(val nextScreen: WelcomeScreen = Screen2(), val position: Int = 0) : WelcomeScreen(position)
    data class Screen2(val nextScreen: WelcomeScreen = GetStarted(), val position: Int = 1) : WelcomeScreen(position)
    data class GetStarted(val position: Int = 2) : WelcomeScreen(position)
}
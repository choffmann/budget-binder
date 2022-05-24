package de.hsfl.budgetBinder.prototype.screens.welcome

import androidx.compose.foundation.background
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


private val welcomeScreenState: MutableState<WelcomeScreen> = mutableStateOf(WelcomeScreen.Screen1())

@Composable
fun WelcomeComponent() {
    WelcomeView()
}

@Composable
private fun WelcomeView() {
    when (welcomeScreenState.value) {
        is WelcomeScreen.Screen1 -> {
            Column {
                ImageWelcomeScreen1(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
                WelcomeText(
                    title = "Welcome to Budget Binder",
                    subtitle = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"
                )
                BottomButtons(onNext = { welcomeScreenState.value = (welcomeScreenState.value as WelcomeScreen.Screen1).nextScreen },
                    onSkip = { welcomeScreenState.value = WelcomeScreen.GetStarted },
                    currentScreen = welcomeScreenState.value.toInt()
                )
            }
        }
        is WelcomeScreen.Screen2 -> {
            Column {
                ImageWelcomeScreen2(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
                WelcomeText(
                    title = "How you can save your money",
                    subtitle = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"
                )
                BottomButtons(onNext = { welcomeScreenState.value = (welcomeScreenState.value as WelcomeScreen.Screen2).nextScreen },
                    onSkip = { welcomeScreenState.value = WelcomeScreen.GetStarted },
                    currentScreen = welcomeScreenState.value.toInt()
                )
            }
        }
        is WelcomeScreen.GetStarted -> {
            Column {
                ImageWelcomeScreen3(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))
                WelcomeText(
                    title = "So, let's get started",
                    subtitle = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At"
                )
                GetStartedButton(onLogin = {}, onRegister = {})
            }
        }
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
private fun BottomButtons(onNext: () -> Unit, onSkip: () -> Unit, currentScreen: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.align(Alignment.BottomCenter), verticalAlignment = Alignment.Bottom
        ) {
            OutlinedButton(modifier = Modifier.padding(16.dp).wrapContentWidth(Alignment.Start).weight(1F),
                onClick = { onSkip() }) {
                Text("SKIP")
            }

            Stepper(
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center).weight(1F),
                total = WelcomeScreen::class.nestedClasses.size,
                isActive = currentScreen
            )

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
fun Stepper(modifier: Modifier, total: Int, isActive: Int) {
    Box(modifier = modifier) {
        Row {
            for (i in (0 until total)) {
                if (i == isActive) {
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
}

@Composable
expect fun ImageWelcomeScreen1(modifier: Modifier = Modifier)

@Composable
expect fun ImageWelcomeScreen2(modifier: Modifier = Modifier)

@Composable
expect fun ImageWelcomeScreen3(modifier: Modifier = Modifier)

sealed class WelcomeScreen {
    data class Screen1(val nextScreen: WelcomeScreen = Screen2()) : WelcomeScreen()
    data class Screen2(val nextScreen: WelcomeScreen = GetStarted) : WelcomeScreen()
    object GetStarted : WelcomeScreen()
}

fun WelcomeScreen.toInt(): Int {
    return when (this) {
        is WelcomeScreen.Screen1 -> 0
        is WelcomeScreen.Screen2 -> 1
        is WelcomeScreen.GetStarted -> 2
    }
}



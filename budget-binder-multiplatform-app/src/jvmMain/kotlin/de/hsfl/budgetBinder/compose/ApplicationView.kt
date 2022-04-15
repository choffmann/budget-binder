package de.hsfl.budgetBinder.compose

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.ApplicationFlow
import de.hsfl.budgetBinder.UIState
import de.hsfl.budgetBinder.client.Client
import de.hsfl.budgetBinder.model.HelloWorld
import de.hsfl.budgetBinder.model.Platform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Composable
fun ApplicationView(image: Painter) {
    val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())
    val applicationFlow = ApplicationFlow(Client(), scope)
    val uiState by applicationFlow.uiState.collectAsState(scope)
    MaterialTheme {
        Column {
            if(uiState is UIState.Success) {
                Text((uiState as UIState.Success).users[0].name)
            }
        }
    }
    /*val showText = remember { mutableStateOf(false) }
    MaterialTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
        {
            Column {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center)
                        .clickable(onClick = {
                            showText.value = !showText.value
                        })
                )
                AnimatedVisibility(
                    visible = showText.value,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    ) {
                        Text(
                            HelloWorld().msg,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h4,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "from ${Platform().platform}",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }
            }
        }
    }*/
}
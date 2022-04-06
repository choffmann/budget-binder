package de.hsfl.budgetBinder.compose

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.HelloWorld
import de.hsfl.budgetBinder.common.Platform

@Composable
fun ApplicationView(image: Painter) {
    val showText = remember { mutableStateOf(false) }
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
                        .clickable(onClick = {
                            showText.value = !showText.value
                        })
                )
                AnimatedVisibility(
                    visible = showText.value,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.Center)
                    ) {
                        if (showText.value) {

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
        }
    }
}
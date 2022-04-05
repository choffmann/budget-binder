package de.hsfl.budgetBinder.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.HelloWorld


@Composable
fun ApplicationView() {
    val model = HelloWorld()
    MaterialTheme {
        Box(modifier = Modifier
                .background(MaterialTheme.colors.surface)
                .fillMaxSize())
        {
            Column(modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .wrapContentSize(Alignment.Center)
                    .clip(shape = RoundedCornerShape(16.dp))
            ) {
                Text(model.msg,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
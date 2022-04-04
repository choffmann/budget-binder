package de.hsfl.budgetBinder.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.common.HelloWorld

@Composable
fun ApplicationView() {
    val obj = HelloWorld()
    MaterialTheme {
        Column {
            Text(obj.msg)
        }
    }
}
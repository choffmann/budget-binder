package de.hsfl.budgetBinder.prototype.screens.ftux

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.StateManager

@Composable
fun SetBudget() {
    Column {
        TextHeader(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            title = "Passe dein Budget so an, wie es dir passt!",
            text = "Hier kannst du dein Budget für jede Kategorie individuell anpassen"
        )
        StateManager.selectedCategories.forEach {
            Text(it.name)
        }
    }
}
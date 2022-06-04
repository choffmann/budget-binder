package de.hsfl.budgetBinder.prototype.screens.ftux

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.StateManager
import de.hsfl.budgetBinder.prototype.StateManager.selectedCategories

@Composable
fun SetBudget() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextHeader(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            title = "Passe dein Budget so an, wie es dir passt!",
            text = "Hier kannst du dein Budget für jede Kategorie individuell anpassen"
        )
        TotalBudget(modifier = Modifier.fillMaxWidth())
        Categories()
    }
}

@Composable
private fun TotalBudget(modifier: Modifier = Modifier) {
    val totalBudget: MutableState<Double> = mutableStateOf(23.49)
    // TODO: Background?
    Text(
        modifier = modifier,
        text = "${totalBudget.value} €",
        style = MaterialTheme.typography.h5,
        fontWeight = FontWeight.Bold,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Categories() {
    LazyColumn {
        items(selectedCategories) {
            ListItem(icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = null) },
                text = { Text(it.name) },
                // TODO: Double in format 0.00
                trailing = { Text("${it.budget}€") })
        }
    }
}
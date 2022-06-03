package de.hsfl.budgetBinder.prototype.screens.ftux

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.Category
import de.hsfl.budgetBinder.prototype.StateManager
import de.hsfl.budgetBinder.prototype.allCategories

@Composable
fun ChooseCategories(onContinue: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        TextHeader(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            title = "Kategorie",
            text = "Wähle die Kategorie, für die du ein Budget erstellen möchtest"
        )
        CategoriesListView(modifier = Modifier.fillMaxWidth().padding(16.dp).weight(1F), onItemClick = {
            if (!StateManager.selectedCategories.contains(it)) {
                StateManager.selectedCategories.add(it)
            } else {
                StateManager.selectedCategories.remove(it)
            }
        })
        Button(modifier = Modifier.fillMaxWidth().padding(16.dp), onClick = { onContinue() }) {
            Text(modifier = Modifier.padding(8.dp), text = "Weiter")
        }
    }
}

@Composable
private fun CategoriesListView(modifier: Modifier = Modifier, onItemClick: (Category) -> Unit) {
    LazyColumn(modifier = modifier) {
        items(allCategories) {
            CategoryRow(category = it, onItemClick = onItemClick)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CategoryRow(category: Category, onItemClick: (Category) -> Unit) {
    ListItem(modifier = Modifier.clickable(onClick = { onItemClick(category) }),
        icon = { Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = null) },
        text = { Text(category.name) },
        trailing = {
            if (StateManager.selectedCategories.contains(category)) Icon(
                imageVector = Icons.Filled.Check, contentDescription = null
            )
        })
}
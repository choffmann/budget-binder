package de.hsfl.budgetBinder.prototype.screens.welcome

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.prototype.Category
import de.hsfl.budgetBinder.prototype.allCategories
import kotlinx.coroutines.launch

val selectedCategories = mutableStateListOf<Category>()

@Composable
fun ChooseCategories() {
    Column(modifier = Modifier.fillMaxSize()) {
        TextHeader(modifier = Modifier.fillMaxWidth().padding(16.dp))
        CategoriesListView(modifier = Modifier.fillMaxWidth().padding(16.dp).weight(1F), onItemClick = {
            if (!selectedCategories.contains(it)) {
                selectedCategories.add(it)
            } else {
                selectedCategories.remove(it)
            }

        })
        Button(modifier = Modifier.fillMaxWidth().padding(16.dp), onClick = {}) {
            Text(modifier = Modifier.padding(8.dp), text = "Weiter")
        }
    }
}

@Composable
private fun TextHeader(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "Kategorie",
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Bold
        )
        Text(text = "Wähle die Kategorie, für die du ein Budget erstellen möchtest", textAlign = TextAlign.Center)
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
            if (selectedCategories.contains(category)) Icon(imageVector = Icons.Filled.Check, contentDescription = null)
        })
}
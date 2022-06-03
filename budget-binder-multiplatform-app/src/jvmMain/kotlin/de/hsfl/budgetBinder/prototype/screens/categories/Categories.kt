package de.hsfl.budgetBinder.prototype.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.hsfl.budgetBinder.prototype.Category
import de.hsfl.budgetBinder.prototype.PrototypeScreen
import de.hsfl.budgetBinder.prototype.allCategories

@Composable
fun CategoriesComponent() {
    CategoriesListView()
}

@Composable
fun CategoriesListView(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(allCategories) {
            CategoryRow(it)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryRow(category: Category) {
    ListItem(
        icon = { Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = null) },
        text = { Text(category.name) },
        trailing = { Icon(imageVector = Icons.Filled.Check, contentDescription = null) }
    )
}
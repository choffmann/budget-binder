package de.hsfl.budgetBinder.screens.category

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.viewmodel.category.detail.CategoryDetailViewModel
import org.kodein.di.instance

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryDetailView() {
    val viewModel: CategoryDetailViewModel by di.instance()
    val categoryState = viewModel.categoryState.collectAsState()
    val entryListState = viewModel.entryList.collectAsState()

    Column {
        Text(categoryState.value.toString())
        LazyColumn {
            items(entryListState.value) { entry ->
                ListItem(text = { Text(entry.name) }, trailing = { Text(entry.amount.toString()) })
            }
        }
    }
}

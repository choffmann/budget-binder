package de.hsfl.budgetBinder.screens.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import de.hsfl.budgetBinder.common.Category

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EntryFormular(
    entryName: String,
    entryAmount: Float,
    entryRepeat: Boolean,
    entryAmountSign: Boolean,
    categoryId: Int?,
    categoryList: List<Category>,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (Float) -> Unit,
    onRepeatChanged: (Boolean) -> Unit,
    onAmountSignChanged: (Boolean) -> Unit,
    onCategoryIdChanged: (Int?) -> Unit
) {
    Column {
        TextField(
            value = entryName,
            onValueChange = onNameChanged,
            label = { Text("Entry Names") }
        )
        TextField(
            value = entryAmount.toString(),
            onValueChange = { onAmountChanged(it.toFloat()) },
            label = { Text("Entry Amount") }
        )
        Row {
            Text("Repeat")
            Checkbox(
                checked = entryRepeat,
                onCheckedChange = onRepeatChanged
            )
        }
        Row {
            Text("Einname")
            Checkbox(
                checked = entryAmountSign,
                onCheckedChange = onAmountSignChanged
            )
        }

        Divider()
        LazyColumn {
            items(categoryList) { category ->
                ListItem(
                    text = { Text(category.toString()) }
                )
            }
        }
    }
}

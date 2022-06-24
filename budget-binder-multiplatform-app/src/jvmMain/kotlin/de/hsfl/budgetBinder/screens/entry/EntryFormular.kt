package de.hsfl.budgetBinder.screens.entry

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.icon.EuroIcon
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.screens.category.toColor

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
    onCategoryIdChanged: (Int?) -> Unit,
    onCancel: () -> Unit
) {
    val category = categoryList.first { it.id == categoryId }
    Card(modifier = Modifier.defaultMinSize(minWidth = 200.dp).fillMaxWidth().padding(16.dp), elevation = 15.dp) {
        BoxWithConstraints {
            if (minWidth >= 500.dp) {
                EntryEditOnLarge(
                    entryName = entryName,
                    entryAmount = entryAmount,
                    entryAmountSign = entryAmountSign,
                    entryRepeat = entryRepeat,
                    category = category,
                    categoryList = categoryList,
                    onCancel = onCancel,
                    onNameChanged = onNameChanged,
                    onAmountChanged = onAmountChanged,
                    onCategoryIdChanged = onCategoryIdChanged,
                    onRepeatChanged = onRepeatChanged,
                    onAmountSignChanged = onAmountSignChanged
                )
            } else {
                EntryEditOnSmall(
                    entryName = entryName,
                    entryAmount = entryAmount,
                    entryRepeat = entryRepeat,
                    entryAmountSign = entryAmountSign,
                    category = category,
                    categoryList = categoryList,
                    onCancel = onCancel,
                    onNameChanged = onNameChanged,
                    onAmountChanged = onAmountChanged,
                    onCategoryIdChanged = onCategoryIdChanged,
                    onRepeatChanged = onRepeatChanged,
                    onAmountSignChanged = onAmountSignChanged
                )
            }
        }
    }
}

@Composable
fun EntryEditOnLarge(
    entryName: String,
    entryAmount: Float,
    entryAmountSign: Boolean,
    entryRepeat: Boolean,
    category: Category,
    categoryList: List<Category>,
    onCancel: () -> Unit,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (Float) -> Unit,
    onCategoryIdChanged: (Int?) -> Unit,
    onRepeatChanged: (Boolean) -> Unit,
    onAmountSignChanged: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        EntryEditName(entryName = entryName, onNameChanged = onNameChanged)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EntryEditAmount(
                modifier = Modifier.weight(1f),
                amount = entryAmount,
                amountSign = entryAmountSign,
                onAmountChanged = onAmountChanged,
                onAmountSignChanged = onAmountSignChanged
            )
            EntryEditCategory(
                modifier = Modifier.weight(1f),
                category = category,
                categoryList = categoryList,
                onCategoryChoose = onCategoryIdChanged
            )
            EntryEditRepeat(modifier = Modifier.weight(1f), repeat = entryRepeat, onRepeatChanged = onRepeatChanged)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCancel) {
            Text("Go Back")
        }
    }
}

@Composable
fun EntryEditOnSmall(
    entryName: String,
    entryAmount: Float,
    entryRepeat: Boolean,
    entryAmountSign: Boolean,
    category: Category,
    categoryList: List<Category>,
    onCancel: () -> Unit,
    onNameChanged: (String) -> Unit,
    onAmountChanged: (Float) -> Unit,
    onCategoryIdChanged: (Int?) -> Unit,
    onRepeatChanged: (Boolean) -> Unit,
    onAmountSignChanged: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        EntryEditName(entryName = entryName, onNameChanged = onNameChanged)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            EntryEditAmount(
                modifier = Modifier.weight(1f),
                amount = entryAmount,
                amountSign = entryAmountSign,
                onAmountChanged = onAmountChanged,
                onAmountSignChanged = onAmountSignChanged
            )
            EntryEditRepeat(modifier = Modifier.weight(1f), repeat = entryRepeat, onRepeatChanged = onRepeatChanged)
        }
        EntryEditCategory(
            modifier = Modifier.weight(1f),
            category = category,
            categoryList = categoryList,
            onCategoryChoose = onCategoryIdChanged
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCancel) {
            Text("Go Back")
        }
    }
}

@Composable
fun EntryEditName(
    modifier: Modifier = Modifier, entryName: String, onNameChanged: (String) -> Unit
) {
    TextField(modifier = modifier, value = entryName, onValueChange = onNameChanged, label = { Text("Entry Name") })
}

@Composable
fun EntryEditCategory(
    modifier: Modifier = Modifier, category: Category, categoryList: List<Category>, onCategoryChoose: (Int) -> Unit
) {
    val expandCategory = remember { mutableStateOf(false) }
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Category", fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { expandCategory.value = !expandCategory.value }) {
            Box(
                modifier = Modifier.shadow(15.dp, CircleShape).clip(CircleShape)
                    .background(category.color.toColor("af"))
            ) {
                Box(modifier = Modifier.align(Alignment.Center).padding(12.dp)) {
                    CategoryImageToIcon(category.image)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = category.name)
            EntryDropDownCategory(
                expand = expandCategory.value,
                categoryList = categoryList,
                onDismiss = { expandCategory.value = false },
                onItemClicked = onCategoryChoose
            )
        }
    }
}

@Composable
fun EntryEditAmount(
    modifier: Modifier = Modifier,
    amount: Float,
    amountSign: Boolean,
    onAmountChanged: (Float) -> Unit,
    onAmountSignChanged: (Boolean) -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            Text("Income")
            Switch(checked = amountSign, onCheckedChange = onAmountSignChanged)
            Text("Output")
        }
        TextField(value = if (amountSign) "+ $amount" else "- $amount",
            onValueChange = { onAmountChanged(it.toFloat()) },
            label = { Text("Entry Amount") },
            leadingIcon = {
                if (amountSign) Icon(Icons.Default.Add, contentDescription = null)
            },
            trailingIcon = {
                EuroIcon()
            })
    }
}

@Composable
fun EntryEditRepeat(
    modifier: Modifier = Modifier, repeat: Boolean, onRepeatChanged: (Boolean) -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Repeat", fontWeight = FontWeight.Bold)
        Checkbox(checked = repeat, enabled = true, onCheckedChange = onRepeatChanged)
    }
}

@Composable
expect fun EntryDropDownCategory(
    modifier: Modifier = Modifier,
    categoryList: List<Category>,
    expand: Boolean,
    onDismiss: () -> Unit,
    onItemClicked: (Int) -> Unit
)


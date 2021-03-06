package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.screens.category.CategoryIconBubble
import de.hsfl.budgetBinder.screens.category.CategoryListItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun PickIconDialog(
    openDialog: Boolean,
    categoryName: String,
    categoryBudget: Float,
    onConfirm: (Category.Image) -> Unit,
    onDismiss: () -> Unit,
    selectColor: Color
) {
    val rememberIcon = remember { allAvailableCategoryIcons() }
    val selectedIcon = remember { mutableStateOf(Category.Image.DEFAULT) }
    val size = 50.dp
    val padding = 8.dp
    if (openDialog) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text(text = "Choose a Icon for the Category") },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(selectedIcon.value)
                    onDismiss()
                }) {
                    Text(text = "Confirm")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CategoryListItem(
                        name = categoryName,
                        budget = categoryBudget.toString(),
                        icon = selectedIcon.value,
                        color = selectColor
                    )
                    LazyVerticalGrid(
                        cells = GridCells.Adaptive(size + padding),
                        contentPadding = PaddingValues(padding),
                        horizontalArrangement = Arrangement.spacedBy(padding),
                        verticalArrangement = Arrangement.spacedBy(padding)
                    ) {
                        items(rememberIcon) { icon ->
                            CategoryIconBubble(
                                size = size,
                                hasBorder = selectedIcon.value == icon,
                                onClick = { selectedIcon.value = icon },
                                icon = icon
                            )
                        }
                    }
                }
            })
    }
}

fun allAvailableCategoryIcons() = listOf(
    Category.Image.DEFAULT,
    Category.Image.CHECKMARK,
    Category.Image.WRONG,
    Category.Image.SHOPPINGCART,
    Category.Image.SHOPPINGBASKET,
    Category.Image.FOOD,
    Category.Image.FASTFOOD,
    Category.Image.RESTAURANT,
    Category.Image.MONEY,
    Category.Image.HOME,
    Category.Image.FAMILY,
    Category.Image.HEALTH,
    Category.Image.MEDICATION,
    Category.Image.KEYBOARD,
    Category.Image.PRINTER,
    Category.Image.INVEST,
    Category.Image.SPORT,
    Category.Image.CLOTH,
    Category.Image.GIFT,
    Category.Image.WEALTH,
    Category.Image.FLOWER,
    Category.Image.PET,
    Category.Image.BILLS,
    Category.Image.WATER,
    Category.Image.FIRE,
    Category.Image.STAR,
    Category.Image.SAVINGS,
    Category.Image.CAR,
    Category.Image.BIKE,
    Category.Image.TRAIN,
    Category.Image.MOTORCYCLE,
    Category.Image.MOPED,
    Category.Image.ELECTRONICS,
    Category.Image.BOOK,
    Category.Image.FLIGHT,
    Category.Image.WORK,
    Category.Image.MOON,
    Category.Image.LOCK,
    Category.Image.PHONE,
    Category.Image.STORE,
    Category.Image.BAR,
    Category.Image.FOREST,
    Category.Image.HARDWARE,
    Category.Image.PEST
)

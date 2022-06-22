package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.screens.category.CategoryColors
import de.hsfl.budgetBinder.screens.category.CategoryListItem
import de.hsfl.budgetBinder.screens.category.toColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun PickColorDialog(
    openDialog: Boolean,
    categoryName: String,
    categoryColor: String,
    categoryImage: Category.Image,
    categoryBudget: Float,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val animatableColor = remember { Animatable(categoryColor.toColor("af")) }
    val rememberColorString = remember { mutableStateOf(categoryColor) }
    val colorList = remember { CategoryColors.colors }
    if (openDialog) {
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text(text = "Choose a Color for the Category") },
            confirmButton = {
                TextButton(onClick = { onConfirm(rememberColorString.value) }) {
                    Text(text = "Confirm")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CategoryListItem(
                        name = categoryName,
                        budget = categoryBudget.toString(),
                        icon = categoryImage,
                        color = animatableColor.value
                    )
                    LazyVerticalGrid(
                        cells = GridCells.Fixed(4), contentPadding = PaddingValues(8.dp)
                    ) {
                        colorList.forEach { (color, colorString) ->
                            item {
                                Box(modifier = Modifier
                                    .size(50.dp)
                                    .shadow(15.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = 3.dp,
                                        color = if (colorString == rememberColorString.value) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        scope.launch {
                                            animatableColor.animateTo(
                                                targetValue = color, animationSpec = tween(durationMillis = 500)
                                            )
                                        }
                                        rememberColorString.value = colorString
                                    })
                            }
                        }
                    }
                }
            })
    }
}

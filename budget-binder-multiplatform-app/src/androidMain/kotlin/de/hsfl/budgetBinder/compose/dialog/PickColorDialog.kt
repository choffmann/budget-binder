package de.hsfl.budgetBinder.compose.dialog

import android.util.Log
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.screens.category.CategoryColorBubble
import de.hsfl.budgetBinder.screens.category.CategoryColors
import de.hsfl.budgetBinder.screens.category.CategoryListItem
import de.hsfl.budgetBinder.screens.category.toColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun PickColorDialog(
    openDialog: Boolean,
    categoryName: String,
    categoryColor: Color,
    categoryImage: Category.Image,
    categoryBudget: Float,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val animatableColor = remember { Animatable(categoryColor) }
    val rememberColorString = remember { mutableStateOf("") }
    val colorList = remember { CategoryColors.colors }
    val size = 50.dp
    val padding = 8.dp
    if (openDialog) {
        Log.d("DialogDebug", "size - padding: ${size - padding}")
        AlertDialog(onDismissRequest = onDismiss,
            title = { Text(text = "Choose a Color for the Category") },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(rememberColorString.value)
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
                        icon = categoryImage,
                        color = animatableColor.value
                    )
                    LazyVerticalGrid(
                        cells = GridCells.Adaptive(size + padding),
                        contentPadding = PaddingValues(padding),
                        verticalArrangement = Arrangement.spacedBy(padding),
                        horizontalArrangement = Arrangement.spacedBy(padding)
                    ) {
                        colorList.forEach { (color, colorString) ->
                            val colorInt = color.toArgb()
                            item {
                                CategoryColorBubble(size = size,
                                    hasBorder = colorInt == animatableColor.value.toArgb(),
                                    backgroundColor = color,
                                    onClick = {
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

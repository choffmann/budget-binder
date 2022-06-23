package de.hsfl.budgetBinder.compose.dialog

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.screens.category.CategoryColorBubble
import de.hsfl.budgetBinder.screens.category.CategoryColors
import de.hsfl.budgetBinder.screens.category.CategoryIconBubble
import de.hsfl.budgetBinder.screens.category.toColor
import kotlinx.coroutines.launch

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
    AnimatedVisibility(
        visible = openDialog,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(size + padding),
            contentPadding = PaddingValues(padding),
            horizontalArrangement = Arrangement.spacedBy(padding),
            verticalArrangement = Arrangement.spacedBy(padding)
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
                            onConfirm(rememberColorString.value)
                        })
                }
            }
        }
    }
}

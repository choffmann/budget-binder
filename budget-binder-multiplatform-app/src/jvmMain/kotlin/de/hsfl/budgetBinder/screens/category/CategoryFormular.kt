package de.hsfl.budgetBinder.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.dialog.PickColorDialog
import de.hsfl.budgetBinder.compose.dialog.PickIconDialog
import de.hsfl.budgetBinder.compose.icon.EuroIcon
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon

@Composable
fun CategoryFormular(
    categoryNameState: String,
    categoryColorState: Color,
    categoryImageState: Category.Image,
    categoryBudgetState: Float,
    onEnteredCategoryName: (String) -> Unit,
    onEnteredCategoryColor: (String) -> Unit,
    onEnteredCategoryImage: (Category.Image) -> Unit,
    onEnteredCategoryBudget: (Float) -> Unit,
    onCancel: () -> Unit,
) {
    val iconDialogState = remember { mutableStateOf(false) }
    val colorDialogState = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.defaultMinSize(
                minWidth = TextFieldDefaults.MinWidth, minHeight = TextFieldDefaults.MinHeight
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pick your Color", fontWeight = FontWeight.Bold)
                Box {
                    CategoryColorBubble(size = 50.dp,
                        hasBorder = true,
                        backgroundColor = categoryColorState,
                        onClick = {
                            iconDialogState.value = false
                            colorDialogState.value = !colorDialogState.value
                        })
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).clip(CircleShape).background(Color.White)
                    ) {
                        if (colorDialogState.value) Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.rotate(180f)
                        )
                        else Icon(
                            Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black
                        )
                    }
                }

            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pick your Icon", fontWeight = FontWeight.Bold)
                Box {
                    CategoryIconBubble(size = 50.dp, hasBorder = true, icon = categoryImageState, onClick = {
                        colorDialogState.value = false
                        iconDialogState.value = !iconDialogState.value
                    })
                    Box(
                        modifier = Modifier.align(Alignment.BottomEnd).clip(CircleShape).background(Color.White)
                    ) {
                        if (iconDialogState.value) Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.rotate(180f)
                        )
                        else Icon(
                            Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black
                        )
                    }
                }

            }
        }
        PickIconDialog(
            categoryName = categoryNameState,
            categoryBudget = categoryBudgetState,
            selectColor = categoryColorState,
            openDialog = iconDialogState.value,
            onConfirm = {
                //iconDialogState.value = false
                onEnteredCategoryImage(it)
            },
            onDismiss = { iconDialogState.value = false },
        )
        PickColorDialog(categoryName = categoryNameState,
            categoryImage = categoryImageState,
            categoryBudget = categoryBudgetState,
            categoryColor = categoryColorState,
            openDialog = colorDialogState.value,
            onDismiss = {
                colorDialogState.value = false
            },
            onConfirm = {
                //colorDialogState.value = false
                onEnteredCategoryColor(it)
            })
        Spacer(modifier = Modifier.height(16.dp))
        TextField(value = categoryNameState,
            singleLine = true,
            onValueChange = { onEnteredCategoryName(it) },
            label = { Text("Category Name") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = categoryBudgetState.toString(),
            singleLine = true,
            onValueChange = { onEnteredCategoryBudget(it.toFloat()) },
            label = { Text("Category Budget") },
            trailingIcon = { EuroIcon() })
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onCancel) {
            Text("Cancel")
        }
    }
}

@Composable
fun CategoryColorBubble(
    size: Dp, hasBorder: Boolean, backgroundColor: Color, onClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(size).shadow(15.dp, CircleShape).clip(CircleShape).background(backgroundColor).border(
            width = 3.dp, color = if (hasBorder) Color.White else Color.Transparent, shape = CircleShape
        ).clickable(onClick = onClick)
    )
}

@Composable
fun CategoryIconBubble(
    size: Dp, hasBorder: Boolean, onClick: () -> Unit, icon: Category.Image
) {
    Box(
        modifier = Modifier.size(size).shadow(15.dp, CircleShape).clip(CircleShape)
            .background(MaterialTheme.colors.onBackground.copy(alpha = TextFieldDefaults.BackgroundOpacity)).border(
                width = 3.dp, color = if (hasBorder) Color.White else Color.Transparent, shape = CircleShape
            ).clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.align(Alignment.Center)) {
            CategoryImageToIcon(icon)
        }
    }
}

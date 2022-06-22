package de.hsfl.budgetBinder.screens.category

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.compose.dialog.PickColorDialog
import de.hsfl.budgetBinder.compose.dialog.PickIconDialog
import de.hsfl.budgetBinder.compose.icon.SaveIcon
import de.hsfl.budgetBinder.di
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditEvent
import de.hsfl.budgetBinder.presentation.viewmodel.category.edit.CategoryEditViewModel
import kotlinx.coroutines.launch
import org.kodein.di.instance

@Composable
fun CategoryEditView() {
    val scope = rememberCoroutineScope()
    val viewModel: CategoryEditViewModel by di.instance()
    val categoryNameState = viewModel.categoryNameState.collectAsState()
    val categoryColorState = viewModel.categoryColorState.collectAsState()
    val animatableColor = remember {
        Animatable(categoryColorState.value.toColor("af"))
    }
    val categoryImageState = viewModel.categoryImageState.collectAsState()
    val categoryBudgetState = viewModel.categoryBudgetState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val iconDialogState = remember { mutableStateOf(false) }
    val colorDialogState = remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.onEvent(CategoryEditEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(CategoryEditEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    Scaffold(scaffoldState = scaffoldState, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.onEvent(CategoryEditEvent.OnSave) }) {
            SaveIcon()
        }
    }) {
        PickIconDialog(
            categoryName = categoryNameState.value,
            categoryBudget = categoryBudgetState.value,
            selectColor = categoryColorState.value.toColor("af"),
            openDialog = iconDialogState.value,
            onConfirm = {
                iconDialogState.value = false
                viewModel.onEvent(CategoryEditEvent.EnteredCategoryImage(it))
            },
            onDismiss = { iconDialogState.value = false },
        )
        PickColorDialog(categoryName = categoryNameState.value,
            categoryImage = categoryImageState.value,
            categoryBudget = categoryBudgetState.value,
            categoryColor = categoryColorState.value,
            openDialog = colorDialogState.value,
            onDismiss = {
                colorDialogState.value = false
            },
            onConfirm = {
                colorDialogState.value = false
                viewModel.onEvent(CategoryEditEvent.EnteredCategoryColor(it))
            })

        Column(modifier = Modifier.fillMaxSize().background(animatableColor.value)) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(start = 32.dp, end = 32.dp).fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pick your Color", fontWeight = FontWeight.Bold)
                    Box {
                        CategoryColorBubble(size = 50.dp,
                            hasBorder = true,
                            backgroundColor = categoryColorState.value.toColor("af"),
                            onClick = { colorDialogState.value = true })
                        Box(
                            modifier = Modifier.align(Alignment.BottomEnd).clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
                        }
                    }

                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Pick your Icon", fontWeight = FontWeight.Bold)
                    Box {
                        CategoryIconBubble(size = 50.dp,
                            hasBorder = true,
                            icon = categoryImageState.value,
                            onClick = { iconDialogState.value = true })
                        Box(
                            modifier = Modifier.align(Alignment.BottomEnd).clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Black)
                        }
                    }

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                TextField(value = categoryNameState.value,
                    singleLine = true,
                    onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryName(it)) },
                    label = { Text("Category Name") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = categoryBudgetState.value.toString(),
                    singleLine = true,
                    onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryBudget(it.toFloat())) },
                    label = { Text("Category Budget") })
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { viewModel.onEvent(CategoryEditEvent.OnCancel) }) {
                    Text("Cancel")
                }
            }
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

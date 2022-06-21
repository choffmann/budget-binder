package de.hsfl.budgetBinder.screens.category

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
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
    val viewModel: CategoryEditViewModel by di.instance()
    val categoryNameState = viewModel.categoryNameState.collectAsState()
    val categoryColorState = viewModel.categoryColorState.collectAsState()
    val animatableColor = remember {
        Animatable(categoryColorState.value.toColor("af"))
    }
    val categoryImageState = viewModel.categoryImageState.collectAsState()
    val categoryBudgetState = viewModel.categoryBudgetState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

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
        Column(modifier = Modifier.fillMaxSize().background(animatableColor.value)) {
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                CategoryColors.colors.forEach { color ->
                    Box(
                        modifier = Modifier.size(50.dp).shadow(15.dp, CircleShape).clip(CircleShape).background(color)
                            .border(
                                width = 3.dp,
                                color = if (color == categoryColorState.value.toColor("af")) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                scope.launch {
                                    animatableColor.animateTo(targetValue = color, animationSpec = tween(durationMillis = 500))
                                }
                            }
                    )
                }
            }
            OutlinedTextField(value = categoryNameState.value,
                onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryName(it)) },
                label = { Text("Category Name") })
            OutlinedTextField(value = categoryColorState.value,
                onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryColor(it)) },
                label = { Text("Category Color") })
            OutlinedTextField(value = categoryBudgetState.value.toString(),
                onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryBudget(it.toFloat())) },
                label = { Text("Category Budget") })
            CategoryImageToIcon(categoryImageState.value)
            Button(onClick = { viewModel.onEvent(CategoryEditEvent.OnCancel) }) {
                Text("Cancel")
            }
        }
    }
}

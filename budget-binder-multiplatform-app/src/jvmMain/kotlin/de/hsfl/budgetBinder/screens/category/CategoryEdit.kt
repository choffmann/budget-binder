package de.hsfl.budgetBinder.screens.category

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import de.hsfl.budgetBinder.common.Category
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
    val dialogState = remember { mutableStateOf(false) }


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
            openDialog = dialogState.value,
            onConfirm = {
                dialogState.value = false
                viewModel.onEvent(CategoryEditEvent.EnteredCategoryImage(it))
            },
            onDismiss = { dialogState.value = false },
            selectColor = animatableColor.value
        )
        Column(modifier = Modifier.fillMaxSize().background(animatableColor.value)) {
            ColorPicker(
                colorState = categoryColorState.value,
                onColorPicked = {
                    scope.launch {
                        animatableColor.animateTo(targetValue = it.first, animationSpec = tween(durationMillis = 500))
                    }
                    viewModel.onEvent(CategoryEditEvent.EnteredCategoryColor(it.second))
                })
            OutlinedTextField(value = categoryNameState.value,
                onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryName(it)) },
                label = { Text("Category Name") })
            OutlinedTextField(value = categoryColorState.value,
                onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryColor(it)) },
                label = { Text("Category Color") })
            OutlinedTextField(value = categoryBudgetState.value.toString(),
                onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryBudget(it.toFloat())) },
                label = { Text("Category Budget") })
            Box(modifier = Modifier.clickable {
                dialogState.value = true
            }) { CategoryImageToIcon(categoryImageState.value) }
            Button(onClick = { viewModel.onEvent(CategoryEditEvent.OnCancel) }) {
                Text("Cancel")
            }
        }
    }
}


@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    colorState: String,
    onColorPicked: (Pair<Color, String>) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp).horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CategoryColors.colors.forEach { (color, colorString) ->
            Spacer(modifier = modifier.width(8.dp))
            Box(
                modifier = Modifier.size(50.dp).shadow(15.dp, CircleShape).clip(CircleShape).background(color)
                    .border(
                        width = 3.dp,
                        color = if (colorString == colorState) Color.White else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable {
                        onColorPicked(Pair(color, colorString))
                    }
            )
        }
    }
}

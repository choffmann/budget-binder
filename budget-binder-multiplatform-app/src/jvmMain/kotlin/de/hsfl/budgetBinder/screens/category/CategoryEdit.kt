package de.hsfl.budgetBinder.screens.category

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
            openDialog = iconDialogState.value,
            onConfirm = {
                iconDialogState.value = false
                viewModel.onEvent(CategoryEditEvent.EnteredCategoryImage(it))
            },
            onDismiss = { iconDialogState.value = false },
            selectColor = animatableColor.value
        )
        PickColorDialog(
            categoryName = categoryNameState.value,
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
            }
        )

        Column(modifier = Modifier.fillMaxSize().background(animatableColor.value)) {
            //Column(modifier = Modifier.fillMaxSize()) {
            Text("Pick your Color")
            Box(modifier = Modifier.size(50.dp).shadow(15.dp, CircleShape).clip(CircleShape)
                    .background(categoryColorState.value.toColor("af"))
                    .border(
                        width = 3.dp,
                        color =  Color.White,
                        shape = CircleShape
                    )
                    .clickable {
                        colorDialogState.value = true
                    }
            )
            /*ColorPicker(
                colorState = categoryColorState.value,
                onColorPicked = {
                    scope.launch {
                        animatableColor.animateTo(targetValue = it.first, animationSpec = tween(durationMillis = 500))
                    }
                    viewModel.onEvent(CategoryEditEvent.EnteredCategoryColor(it.second))
                })*/
            /*Text("Pick a Icon")
            IconPicker(
                colorState = animatableColor.value,
                iconState = categoryImageState.value,
                onSelectedIcon = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryImage(it)) }
            )*/


            Text("Pick your Icon")
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(50.dp).clickable {
                    iconDialogState.value = true
                }) { CategoryImageToIcon(categoryImageState.value) }

                TextField(value = categoryNameState.value,
                    onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryName(it)) },
                    label = { Text("Category Name") })

                /*TextField(value = categoryColorState.value,
                    onValueChange = { viewModel.onEvent(CategoryEditEvent.EnteredCategoryColor(it)) },
                    label = { Text("Category Color") })*/
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = categoryBudgetState.value.toString(),
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

@Composable
fun IconPicker(
    modifier: Modifier = Modifier,
    onSelectedIcon: (Category.Image) -> Unit,
    iconState: Category.Image,
    colorState: Color
) {
    val rememberIcon = remember { allAvailableCategoryIcons() }
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp).horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        rememberIcon.forEach { icon ->
            Spacer(modifier = modifier.width(8.dp))
            Box(
                modifier = Modifier.size(50.dp).shadow(15.dp, CircleShape).clip(CircleShape).background(colorState)
                    .border(
                        width = 3.dp,
                        color = if (iconState == icon) Color.White else Color.Transparent,
                        shape = CircleShape
                    )
                    .clickable {
                        onSelectedIcon(icon)
                    }
            ) {
                Box(modifier = Modifier.align(Alignment.Center)) {
                    CategoryImageToIcon(icon)
                }
            }
        }
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

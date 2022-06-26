package de.hsfl.budgetBinder.screens.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.ColorCircle
import de.hsfl.budgetBinder.compose.DeleteDialog
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.event.LifecycleEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryEvent
import de.hsfl.budgetBinder.presentation.viewmodel.entry.EntryViewModel
import di
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun EntryOverviewView(
    onEditButton: () -> Unit,
    onDeleteButton: () -> Unit,
    onDeleteDialogConfirmButton: () -> Unit,
    onDeleteDialogDismissButton: () -> Unit,
    onCancel: () -> Unit
) {
    val viewModel: EntryViewModel by di.instance()
    //Data
    val entry by viewModel.selectedEntryState.collectAsState()
    val deleteDialog by viewModel.dialogState.collectAsState()
    val categoryList by viewModel.categoryListState.collectAsState()

    //LifeCycle
    LaunchedEffect(Unit) {
        viewModel.onEvent(EntryEvent.LifeCycle(LifecycleEvent.OnLaunch))
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(EntryEvent.LifeCycle(LifecycleEvent.OnDispose))
        }
    }

    //Webpage Content
    H1(attrs = { classes(AppStylesheet.h1) }) { Text(" Entry") }
    EntryOverview(
        entry,
        categoryList,
        deleteDialog,
        onEditButton,
        onDeleteButton,
        onDeleteDialogConfirmButton,
        onDeleteDialogDismissButton,
        onCancel
    )
}

@Composable
fun EntryOverview(
    entry: Entry,
    categoryList: List<Category>,
    deleteDialog: Boolean,
    onEditButton: () -> Unit,
    onDeleteButton: () -> Unit,
    onDeleteDialogConfirmButton: () -> Unit,
    onDeleteDialogDismissButton: () -> Unit,
    onCancel: () -> Unit
) {
    Div(
        attrs = {
            classes(AppStylesheet.categoryListElement, AppStylesheet.flexContainer)
        }
    ) {

        Div(
            attrs = {
                classes(AppStylesheet.categoryListElementText)
            }
        ) {
            Div {
                Div(attrs = {
                    classes("mdc-typography--headline4", AppStylesheet.text)
                }) { Text(entry.name) }
                Div(attrs = { classes(AppStylesheet.flexContainer) }) {
                    Div(attrs = {
                        classes(
                            "mdc-typography--headline6",
                            AppStylesheet.text,
                            AppStylesheet.buttonOverview
                        )
                    }) {
                        var categoryName = "No Category"
                        var categoryIcon = Category.Image.DEFAULT
                        var categoryColor = "111111"
                        for (category in categoryList) {
                            if (entry.category_id == category.id) {
                                categoryName = category.name
                                categoryIcon = category.image
                                categoryColor = category.color
                            }
                        }
                        Text("Category: $categoryName")
                        Div(attrs = { classes(AppStylesheet.flexContainer) }) {
                            CategoryImageToIcon(categoryIcon)
                            ColorCircle(categoryColor)
                        }
                    }
                    Div(attrs = {
                        classes(
                            "mdc-typography--headline6",
                            AppStylesheet.text,
                            AppStylesheet.buttonOverview
                        )
                    }) { Text("Amount: ${entry.amount}€") }
                    Div(attrs = {
                        classes(
                            "mdc-typography--headline6",
                            AppStylesheet.text,
                            AppStylesheet.buttonOverview
                        )
                    }) { Text("Repeat: " + if (entry.repeat) "Yes" else "No") }
                }
            }
        }
    }
    Div(
        attrs = {
            classes(AppStylesheet.flexContainer)
        }
    ) {
        Button(attrs = {
            classes("mdc-button", "mdc-button--raised", AppStylesheet.buttonOverview)
            onClick { onCancel() }
        }
        ) {
            Text("Go back")
        }
        Button(attrs = {
            classes("mdc-button", "mdc-button--raised", AppStylesheet.buttonOverview)
            onClick { onEditButton() }
        }) {
            Text("Edit Entry")
        }
        Button(attrs = {
            classes("mdc-button", "mdc-button--raised", AppStylesheet.buttonOverview)
            onClick { onDeleteButton() }
            style { backgroundColor(Color("#b00020")) }
        }) {
            Text("Delete Entry")
        }
    }
    if (deleteDialog) {
        DeleteDialog(
            false,
            { onDeleteDialogConfirmButton() },
            { onDeleteDialogDismissButton() })
        { Text("Delete Entry?") }
    }
}



package de.hsfl.budgetBinder.compose.entry

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.common.Category
import de.hsfl.budgetBinder.common.Entry
import de.hsfl.budgetBinder.compose.CategoryList
import de.hsfl.budgetBinder.compose.DeleteDialog
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.compose.topBarMain
import de.hsfl.budgetBinder.presentation.CategoryImageToIcon
import de.hsfl.budgetBinder.presentation.UiState
import de.hsfl.budgetBinder.presentation.viewmodel.entryViewModel.EntryViewModel
import di
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.svg.Circle
import org.jetbrains.compose.web.svg.Svg
import org.kodein.di.instance


@Composable
fun EntryOverviewView(
    onEditButton: () -> Unit,
    onDeleteButton: () -> Unit,
    onDeleteDialogConfirmButton: () -> Unit,
    onDeleteDialogDismissButton: () -> Unit
) {
    val viewModel: EntryViewModel by di.instance()
    //Data
    val entry by viewModel.selectedEntryState.collectAsState()
    console.log("Our Entry is $entry")
    val deleteDialog by viewModel.dialogState.collectAsState()
    H1(
        attrs = {
            style { margin(2.percent) }
        }
    ) { Text(" Entry") }

    EntryOverview(
        entry,
        deleteDialog,
        onEditButton,
        onDeleteButton,
        onDeleteDialogConfirmButton,
        onDeleteDialogDismissButton
    )
}

@Composable
fun EntryOverview(
    entry: Entry,
    deleteDialog: Boolean,
    onEditButton: () -> Unit,
    onDeleteButton: () -> Unit,
    onDeleteDialogConfirmButton: () -> Unit,
    onDeleteDialogDismissButton: () -> Unit
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
                Div(attrs = {
                    classes("mdc-typography--headline6", AppStylesheet.text)
                }) { Text("Amount: ${entry.amount}€") }
            }
        }
    }
    Div(
        attrs = {
            classes(AppStylesheet.flexContainer)
        }
    ) {
        Button(attrs = {
            classes("mdc-button", "mdc-button--raised", AppStylesheet.marginRight)
            onClick { onEditButton() }
            style {
                flex(50.percent)
                margin(1.5.percent)
            }
        }) {
            Text("Edit Entry")
        }
        Button(attrs = {
            classes("mdc-button", "mdc-button--raised")
            onClick { onDeleteButton() }
            style {
                flex(50.percent)
                margin(1.5.percent)
                backgroundColor(Color("#b00020"))
            }
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



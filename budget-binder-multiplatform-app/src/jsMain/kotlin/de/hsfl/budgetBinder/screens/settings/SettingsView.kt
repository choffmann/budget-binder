package de.hsfl.budgetBinder.screens.settings

import androidx.compose.runtime.*
import de.hsfl.budgetBinder.compose.DeleteDialog
import de.hsfl.budgetBinder.compose.MainFlexContainer
import de.hsfl.budgetBinder.compose.NavBar
import de.hsfl.budgetBinder.compose.theme.AppStylesheet
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsEvent
import de.hsfl.budgetBinder.presentation.viewmodel.settings.SettingsViewModel
import di
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.kodein.di.instance


@Composable
fun SettingsView() {
    val viewModel: SettingsViewModel by di.instance()
    var deleteDialog by remember { mutableStateOf(false) }

    NavBar { }
    MainFlexContainer {
        H1(attrs = { classes(AppStylesheet.h1) }) { Text("Settings") }
        Div(
            attrs = {
                classes(AppStylesheet.margin, AppStylesheet.flexContainer)
            }
        ) {
            Button(
                attrs = {
                    classes("mdc-button", "mdc-button--raised", AppStylesheet.flex100)
                    onClick { viewModel.onEvent(SettingsEvent.OnChangeToSettingsUserEdit) }
                }
            ) {
                Text("Change Userdata")
            }
        }
        Div(
            attrs = {
                classes(AppStylesheet.margin, AppStylesheet.flexContainer)
            }
        ) {
            Button(
                attrs = {
                    classes("mdc-button", "mdc-button--raised", AppStylesheet.flex100)
                    onClick { viewModel.onEvent(SettingsEvent.OnLogoutAllDevices) }
                }
            ) {
                Text("Logout on all device")
            }
        }
        Div(
            attrs = {
                classes(AppStylesheet.margin, AppStylesheet.flexContainer)
            }
        ) {
            Button(
                attrs = {
                    classes("mdc-button", "mdc-button--raised", AppStylesheet.flex100)
                    onClick { deleteDialog = true }
                    style { backgroundColor(Color("#b00020")) }
                }
            ) {
                Text("Delete User")
            }
        }
    }
    if (deleteDialog) {
        DeleteDialog(
            false,
            { viewModel.onEvent(SettingsEvent.OnDeleteDialogConfirm) },
            { deleteDialog = false }) { Text("Delete User?") }
    }
}

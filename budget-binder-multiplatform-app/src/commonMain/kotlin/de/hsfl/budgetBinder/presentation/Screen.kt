package de.hsfl.budgetBinder.presentation

import de.hsfl.budgetBinder.common.Category

sealed class Screen {
    object Welcome : Screen()
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
    object Settings : Screen()
    object SettingsChangeUserData : Screen()
    object CategorySummary : Screen()
    object CategoryEdit : Screen()
    object CategoryCreate : Screen()
    object CategoryCreateOnRegister : Screen()
    object EntryEdit : Screen()
    data class EntryCreate (val categoryList :List<Category>): Screen()

}

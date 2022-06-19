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
    data class CategoryEdit (val id: Int) : Screen()
    object CategoryCreate : Screen()
    object CategoryCreateOnRegister : Screen()
    data class EntryOverview (val id: Int) : Screen()
    data class EntryEdit (val id: Int): Screen()
    data class EntryCreate (val categoryList :List<Category>): Screen()

}

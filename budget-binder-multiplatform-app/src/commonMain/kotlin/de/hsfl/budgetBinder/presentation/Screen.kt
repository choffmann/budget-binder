package de.hsfl.budgetBinder.presentation

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
    data class EntryOverview (val id: Int) : Screen()
    data class EntryEdit (val id: Int): Screen()
    object EntryCreate : Screen()

}

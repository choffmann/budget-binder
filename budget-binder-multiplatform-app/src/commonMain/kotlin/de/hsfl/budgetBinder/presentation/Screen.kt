package de.hsfl.budgetBinder.presentation

sealed class Screen {
    object Welcome : Screen()
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
    object Settings : Screen()
    object CategorySummary : Screen()
    object CategoryEdit : Screen()
    object CategoryCreate : Screen()
    object CategoryCreateOnRegister : Screen()
    object EntryEdit : Screen()
    object EntryCreate : Screen()

}

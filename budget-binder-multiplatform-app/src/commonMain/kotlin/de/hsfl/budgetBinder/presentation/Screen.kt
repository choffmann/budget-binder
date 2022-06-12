package de.hsfl.budgetBinder.presentation

sealed class Screen {
    object Welcome : Screen()
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
    object Settings : Screen()
    object Categories : Screen()
    object CategoryCreate : Screen()
    object CategorySummary : Screen()
    object CategoryEdit : Screen()
    object CategoryCreateOnRegister : Screen()
    object EntryCreate : Screen()
    object EntryEdit : Screen()
}

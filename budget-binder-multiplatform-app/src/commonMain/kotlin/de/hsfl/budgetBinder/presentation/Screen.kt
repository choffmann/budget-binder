package de.hsfl.budgetBinder.presentation

sealed class Screen {
    sealed class Welcome: Screen() {
        object Screen1: Welcome()
        object Screen2: Welcome()
        object GetStarted: Welcome()
    }
    sealed class Settings: Screen() {
        object Menu: Settings()
        object User: Settings()
        object Server: Settings()
    }
    sealed class Category: Screen() {
        object Summary: Category()
        object Edit: Category()
        object Create: Category()
        object CreateOnRegister: Category()
    }
    sealed class Entry: Screen() {
        object Edit: Category()
        object Create: Category()
    }
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
}
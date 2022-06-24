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
        data class Detail(val id: Int): Category()
        object Summary: Category()
        data class Edit(val id: Int): Category()
        object Create: Category()
        object CreateOnRegister: Category()
    }
    sealed class Entry: Screen() {
        data class Overview(val id: Int): Entry()
        data class Edit(val id: Int): Entry()
        object Create: Entry()
    }
    object Login : Screen()
    object Register : Screen()
    object Dashboard : Screen()
}

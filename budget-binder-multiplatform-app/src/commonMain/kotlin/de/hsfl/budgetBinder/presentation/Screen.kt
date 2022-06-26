package de.hsfl.budgetBinder.presentation
sealed class Screen(open val screenWeight: Int = 0) {
    sealed class Welcome(override val screenWeight: Int = 0): Screen(screenWeight = screenWeight) {
        object Screen1: Welcome(screenWeight = 0)
        object Screen2: Welcome(screenWeight = 1)
        object GetStarted: Welcome(screenWeight = 2)
    }
    sealed class Settings(override val screenWeight: Int = 0): Screen(screenWeight = screenWeight) {
        object Menu: Settings(screenWeight = 0)
        object User: Settings(screenWeight = 1)
        object Server: Settings(screenWeight = 1)
    }
    sealed class Category(override val screenWeight: Int = 0): Screen(screenWeight = screenWeight) {
        data class Detail(val id: Int): Category(screenWeight = 1)
        object Summary: Category(screenWeight = 0)
        data class Edit(val id: Int): Category(screenWeight = 2)
        object Create: Category(screenWeight = 2)
        object CreateOnRegister: Category(screenWeight = 0)
    }
    sealed class Entry(override val screenWeight: Int = 0): Screen(screenWeight = screenWeight) {
        data class Overview(val id: Int): Entry(screenWeight = 0)
        data class Edit(val id: Int): Entry(screenWeight = 1)
        object Create: Entry(screenWeight = 1)
    }
    object Login : Screen(screenWeight = 0)
    object Register : Screen(screenWeight = 0)
    object Dashboard : Screen(screenWeight = 1)
}

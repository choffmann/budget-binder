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

    @Deprecated(message = "use sealed class Welcome")
    object _Welcome : Screen()
    @Deprecated(message = "use sealed class Settings")
    object _Settings : Screen()
    @Deprecated(message = "use sealed class Settings")
    object SettingsChangeUserData : Screen()
    @Deprecated(message = "use sealed class Category")
    object CategorySummary : Screen()
    @Deprecated(message = "use sealed class Category")
    data class CategoryEdit(val id: Int) : Screen()
    @Deprecated(message = "use sealed class Category")
    object CategoryCreate : Screen()
    @Deprecated(message = "use sealed class Category")
    object CategoryCreateOnRegister : Screen()
    @Deprecated(message = "use sealed class Category")
    data class EntryOverview (val id: Int) : Screen()
    @Deprecated(message = "use sealed class Entry")
    data class EntryEdit (val id: Int): Screen()
    @Deprecated(message = "use sealed class Entry")
    data class EntryCreate (val categoryList :List<de.hsfl.budgetBinder.common.Category>): Screen()
}

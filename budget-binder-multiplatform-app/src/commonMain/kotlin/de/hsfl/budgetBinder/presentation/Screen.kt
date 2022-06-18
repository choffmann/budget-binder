package de.hsfl.budgetBinder.presentation

import de.hsfl.budgetBinder.domain.usecase.DeleteEntryByIdUseCase

import de.hsfl.budgetBinder.common.Category

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
        data class Overview(val id: Int): Entry()
        data class Edit(val id: Int): Entry()
        data class Create (val categoryList :List<Category>): Entry()
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
    object CategoryEdit : Screen()
    @Deprecated(message = "use sealed class Category")
    object CategoryCreate : Screen()
    @Deprecated(message = "use sealed class Category")
    object CategoryCreateOnRegister : Screen()
    @Deprecated(message = "use sealed class Entry")
    object EntryEdit : Screen()
    @Deprecated(message = "use sealed class Entry")
    object EntryCreate : Screen()
}

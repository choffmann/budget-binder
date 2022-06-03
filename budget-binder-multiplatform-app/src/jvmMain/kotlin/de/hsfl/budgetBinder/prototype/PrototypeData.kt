package de.hsfl.budgetBinder.prototype

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class User(
    val firstName: String = "Jeeve",
    val lastName: String = "Philodendren",
    val password: String = "geheim",
    val email: String = "jeeve@plants.com"
)

data class Server(
    val serverUrl: String = "http://localhost:8080"
)

data class Settings(
    val user: User = User(), val server: Server = Server(), val darkMode: MutableState<Boolean> = mutableStateOf(false)
)

// Categories
data class Category(val name: String)

val allCategories = listOf(
    Category(name = "Lebensmittel"),
    Category(name = "Abonnements"),
    Category(name = "Allgemein"),
    Category(name = "Bargeld"),
    Category(name = "Finanzen"),
    Category(name = "Familie"),
    Category(name = "Shopping"),
    Category(name = "Unterhaltung"),
    Category(name = "Essen und Trinken"),
    Category(name = "Gesundheit & Drogerie"),
    Category(name = "Haushaltskosten"),
    Category(name = "Reisen"),
    Category(name = "Geschäftskosten"),
    Category(name = "Autokosten"),
    Category(name = "Elektronik"),
    Category(name = "Investment"),
    Category(name = "Kultur"),
    Category(name = "Gesundheit"),
    Category(name = "Haustiere"),
    Category(name = "Kleidung"),
    Category(name = "Sport"),
    Category(name = "Geschenke"),
    Category(name = "Gehalt"),
    Category(name = "Personalabteilung"),
    Category(name = "Marketing"),
    Category(name = "Miete und Nebenkosten"),
    Category(name = "Versicherungen"),
    Category(name = "Arbeitnehmerleistungen"),
    Category(name = "Büromaterial"),
    Category(name = "Vermögen"),
    Category(name = "Professionelle Dienstleistungen"),
)
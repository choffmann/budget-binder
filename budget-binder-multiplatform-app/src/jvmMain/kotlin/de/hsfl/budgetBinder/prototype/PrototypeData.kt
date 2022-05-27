package de.hsfl.budgetBinder.prototype

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class User(
    val firstName: String = "Jeeve",
    val lastName: String = "Philodendren",
    val password: String = "geheim",
    val email: String = "philodendren@plants.com"
)

data class Server(
    val serverUrl: String = "http://localhost:8080"
)

data class Settings(
    val user: User = User(),
    val server: Server = Server(),
    val darkMode: MutableState<Boolean> = mutableStateOf(false)
)
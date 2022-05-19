package de.hsfl.budgetBinder.compose

import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.data.repository.AuthRepositoryImpl
import de.hsfl.budgetBinder.data.repository.UserRepositoryImpl
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import de.hsfl.budgetBinder.domain.repository.UserRepository
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.domain.use_case.auth_user.LogoutUseCase
import de.hsfl.budgetBinder.domain.use_case.auth_user.RegisterUseCase
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import de.hsfl.budgetBinder.presentation.Screen
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.compose.withDI
import org.kodein.di.instance

val di = DI {
    bindSingleton { Client() }

    bindSingleton<AuthRepository> { AuthRepositoryImpl(instance()) }
    bindSingleton<UserRepository> { UserRepositoryImpl(instance()) }

    bindSingleton { RegisterUseCase(instance()) }
    bindSingleton { LoginUseCase(instance()) }
    bindSingleton { LogoutUseCase(instance()) }
    bindSingleton { UserUseCase(instance()) }
}

@Composable
fun App() = withDI(di) {
    val screenState = remember { mutableStateOf<Screen>(Screen.Login) }
    val darkTheme = remember { mutableStateOf(false) }
    MaterialTheme(
        colors = if (darkTheme.value) darkColors() else lightColors()
    ) {
        Router(screenState = screenState)

        // Toggle Dark-mode
        IconToggleButton(checked = darkTheme.value, onCheckedChange = { darkTheme.value = it }) {
            if (darkTheme.value)
                Icon(Icons.Filled.Info, contentDescription = null, tint = Color.White)
            else
                Icon(Icons.Filled.Info, contentDescription = null, tint = Color.Black)
        }
    }
}
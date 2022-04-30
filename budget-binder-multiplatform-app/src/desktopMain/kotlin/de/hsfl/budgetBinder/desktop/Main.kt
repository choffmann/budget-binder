package de.hsfl.budgetBinder.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import de.hsfl.budgetBinder.compose.UserView
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.data.repository.AuthRepositoryImplementation
import de.hsfl.budgetBinder.data.repository.UserRepositoryImplementation
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val di = DI {
    bindSingleton { Client() }

    bindSingleton { AuthRepositoryImplementation(instance()) }
    bindSingleton { UserRepositoryImplementation(instance()) }

    bindSingleton { LoginUseCase(instance()) }
    bindSingleton { UserUseCase(instance()) }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        UserView(di)
    }
}
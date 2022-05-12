package de.hsfl.budgetBinder.compose

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.data.repository.AuthRepositoryImplementation
import de.hsfl.budgetBinder.data.repository.UserRepositoryImplementation
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import de.hsfl.budgetBinder.domain.repository.UserRepository
import de.hsfl.budgetBinder.domain.use_case.auth_user.LoginUseCase
import de.hsfl.budgetBinder.domain.use_case.get_user.UserUseCase
import de.hsfl.budgetBinder.presentation.component.root.AppRoot
import org.kodein.di.*
import org.kodein.di.compose.withDI

val di = DI {
    bindSingleton { Client() }

    bindSingleton<AuthRepository> { AuthRepositoryImplementation(instance()) }
    bindSingleton<UserRepository> { UserRepositoryImplementation(instance()) }

    bindSingleton { LoginUseCase(instance()) }
    bindSingleton { UserUseCase(instance()) }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun App(component: AppRoot) = withDI(di) {
    Children(routerState = component.routerState) {
        when (val child = it.instance) {
            is AppRoot.Child.Login -> LoginViewContent(child.component)
            is AppRoot.Child.User -> UserViewContent(child.component)
        }
    }
}
package de.hsfl.budgetBinder.client

import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.data.client.Client
import de.hsfl.budgetBinder.data.repository.AuthRepositoryImpl
import de.hsfl.budgetBinder.data.repository.CategoryRepositoryImpl
import de.hsfl.budgetBinder.data.repository.EntryRepositoryImpl
import de.hsfl.budgetBinder.data.repository.UserRepositoryImpl
import de.hsfl.budgetBinder.domain.repository.AuthRepository
import de.hsfl.budgetBinder.domain.repository.CategoryRepository
import de.hsfl.budgetBinder.domain.repository.EntryRepository
import de.hsfl.budgetBinder.domain.repository.UserRepository
import de.hsfl.budgetBinder.domain.usecase.auth.LoginUseCase
import de.hsfl.budgetBinder.domain.usecase.auth.LogoutUseCase
import de.hsfl.budgetBinder.domain.usecase.auth.RegisterUseCase
import org.kodein.di.DI
import org.kodein.di.bindConstant
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import kotlin.test.Test

val di = DI {
    bindConstant(tag = "serverUrl") { "http://localhost:8080" }
    bindSingleton { Client(instance("serverUrl")) }

    bindSingleton<AuthRepository> { AuthRepositoryImpl(instance()) }
    bindSingleton<UserRepository> { UserRepositoryImpl(instance()) }
    bindSingleton<CategoryRepository> { CategoryRepositoryImpl(instance()) }
    bindSingleton<EntryRepository> { EntryRepositoryImpl(instance()) }

    bindSingleton { LoginUseCase(instance()) }
    bindSingleton { RegisterUseCase(instance()) }
    bindSingleton { LogoutUseCase(instance()) }
}

class AuthTest() {
    @Test
    fun registerWithUser() {
        val user = User.In(firstName = "Test", name = "User", email = "test.user@mail.com", password = "geheim")

    }
}
package de.hsfl.budgetBinder.server.plugins

import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.services.JWTService
import de.hsfl.budgetBinder.server.services.implementations.CategoryServiceImpl
import de.hsfl.budgetBinder.server.services.implementations.EntryServiceImpl
import de.hsfl.budgetBinder.server.services.implementations.UserServiceImpl
import de.hsfl.budgetBinder.server.services.interfaces.CategoryService
import de.hsfl.budgetBinder.server.services.interfaces.EntryService
import de.hsfl.budgetBinder.server.services.interfaces.UserService
import io.ktor.server.application.*
import org.kodein.di.bindEagerSingleton
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import org.kodein.di.ktor.di

fun Application.configureDI(config: Config) {
    di {
        bindEagerSingleton { config }
        bindSingleton<UserService> { UserServiceImpl() }
        bindSingleton<EntryService> { EntryServiceImpl() }
        bindSingleton<CategoryService> { CategoryServiceImpl() }
        bindSingleton { JWTService(instance()) }
    }
}

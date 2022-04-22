package de.hsfl.budgetBinder.server


import de.hsfl.budgetBinder.server.models.Roles

import de.hsfl.budgetBinder.server.routes.userRoutes
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.transactions.transaction

import org.kodein.di.*
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di


fun Application.module() {
    install(CORS) {
        anyHost()
    }
    install(Authentication) {
        basic("auth-basic") {
            realm = "Budget Binder Server"
            validate {
                val userService: UserService by closestDI().instance()
                userService.findUserByEmailAndPassword(it.name, it.password)
            }
        }
        basic("auth-basic-admin") {
            realm = "Budget Binder Server Admin"
            validate {
                val userService: UserService by closestDI().instance()
                val user = userService.findUserByEmailAndPassword(it.name, it.password)

                transaction {
                    if (user?.role == Roles.ADMIN) user else null
                }
            }
        }
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate {
                val userService: UserService by closestDI().instance()
                userService.findUserByEmailAndPassword(it.name, it.password)
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    di {
        bindSingleton { UserService() }
    }

    // install all Modules
    userRoutes()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    routing {
        get("/path") {
            val userService: UserService by closestDI().instance()
            val user = userService.getRandomUser()
            call.respondText(user.email)
        }
    }
}

package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.routes.userRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction


fun Application.module() {
    install(CORS) {
        anyHost()
    }
    install(Authentication) {
        basic("auth-basic") {
            realm = "Budget Binder Server"
            validate {
                UserIdPrincipal(it.name)
            }
        }
    }

    install(ContentNegotiation) {
        json()
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
            val user = transaction {
                UserEntity.all().toList().random()
            }
            call.respondText(user.email)
        }
    }
}

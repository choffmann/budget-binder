package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.routes.userRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.runBlocking
import java.lang.Integer.parseInt

fun main() = runBlocking<Unit> {
    val port = parseInt(System.getenv("HOME") ?: "8080")

    embeddedServer(Netty, port = port, watchPaths = listOf("classes", "resources")) {
        module()
    }.start(wait = true)
}

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
}
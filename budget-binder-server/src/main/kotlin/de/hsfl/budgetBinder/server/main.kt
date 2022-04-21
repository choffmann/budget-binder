package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.server.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.auth.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val port = Integer.parseInt(System.getenv("PORT") ?: "8080")
    val host = System.getenv("HOST") ?: "0.0.0.0"

    /*
    * configure = {
    *   https://ktor.io/docs/engines.html#engine-main-configure
    *   https://api.ktor.io/ktor-server/ktor-server-netty/io.ktor.server.netty/-netty-application-engine/-configuration/index.html#2119802284%2FProperties%2F1117634132
    *   requestQueueLimit = 16
    *   shareWorkGroup = false
    *   configureBootstrap = {
    *       // ...
    *   }
    *   responseWriteTimeoutSeconds = 10
    * }
    * */
    embeddedServer(Netty, port = port, watchPaths = listOf("classes", "resources"), host = host) {
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

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
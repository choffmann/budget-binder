package de.hsfl.budgetBinder.server

import de.hsfl.budgetBinder.common.Post
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*


fun Application.mainModule() {
    install(CORS) {
        anyHost()
    }
    install(Authentication) {
        basic("auth-basic") {
            realm = "KoolerV2 Server"
            validate {
                UserIdPrincipal(it.name)
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }

    routing {
        authenticate("auth-basic") {
            get("/admin") {
                val name = call.principal<UserIdPrincipal>()?.name ?: "test"
                call.respond(Post(id = 1, userId = 1, title = "admin", body = name))
            }
        }

        get("/random") {
            call.respond(Post(id = 1, userId = 1, title = "user", body = "user"))
        }
    }
}
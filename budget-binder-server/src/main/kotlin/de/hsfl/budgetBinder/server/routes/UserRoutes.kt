package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.Post
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.userByIdRoute() {
    authenticate("auth-basic") {
        get("/user/{name}") {
            val name = call.principal<UserIdPrincipal>()?.name ?: "test"
            call.respond(Post(id = 1, userId = 1, title = "admin", body = name))
        }
    }
}

// install all previous Routes
fun Application.userRoutes() {
    routing {
        userByIdRoute()
    }
}

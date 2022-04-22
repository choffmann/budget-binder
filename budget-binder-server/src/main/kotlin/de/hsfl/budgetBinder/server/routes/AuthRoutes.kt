package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.server.models.UserEntity
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.login() {
    authenticate("auth-form") {
        post("/login") {
            val user = call.principal<UserEntity>()!!
            call.respond(APIResponse(user.toDto()))
        }
    }
}

// install all previous Routes
fun Application.authRoutes() {
    routing {
        login()
    }
}

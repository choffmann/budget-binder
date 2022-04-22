package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Post
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


fun Route.userByIdRoute() {
    authenticate("auth-jwt") {
        get("/user/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest,
                    APIResponse<Post>(null,"path parameter is not a number", false))
                return@get
            }

            val userService: UserService by closestDI().instance()
            val user = userService.findUserByID(id)

            if (user == null) {
                call.respond(HttpStatusCode.NotFound,
                    APIResponse<Post>(null,"User not found", false))
                return@get
            }

            val email = call.principal<UserEntity>()!!.email
            call.respond(APIResponse(
                Post(id = 1, userId = 1, title = email, body = user.email)
            ))
        }
    }
}

// install all previous Routes
fun Application.userRoutes() {
    routing {
        userByIdRoute()
    }
}

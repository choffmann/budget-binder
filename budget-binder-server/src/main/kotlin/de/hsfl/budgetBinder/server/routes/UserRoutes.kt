package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.meRoute() {
    authenticate("auth-jwt") {
        get("/users/me") {
            call.respond(APIResponse(call.principal<UserEntity>()!!.toDto()))
        }
    }
}

fun Route.allUsersRoute() {
    authenticate("auth-jwt-admin") {
        get("/users"){
            val userService: UserService by closestDI().instance()
            call.respond(
                APIResponse(userService.getAllUsers().map { it.toDto() })
            )
        }
    }
}

fun Route.userByIdRoute() {
    authenticate("auth-jwt-admin") {
        get("/users/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    APIResponse("error", ErrorModel(error = true, message = "path parameter is not a number"), false)
                )
                return@get
            }

            val userService: UserService by closestDI().instance()
            val user = userService.findUserByID(id)

            if (user == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    APIResponse("error", ErrorModel(error = true, message = "User not found"), false)
                )
                return@get
            }

            call.respond(
                APIResponse(data = user.toDto())
            )
        }
    }
}

// install all previous Routes
fun Application.userRoutes() {
    routing {
        meRoute()
        allUsersRoute()
        userByIdRoute()
    }
}

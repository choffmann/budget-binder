package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.meRoute() {
    authenticate("auth-jwt") {
        route("/users/me") {
            get("") {
                call.respond(APIResponse(call.principal<UserEntity>()!!.toDto()))
            }
            put("") {
                val user = call.principal<UserEntity>()!!
                val userPut: User.Put? = call.receiveOrNull()
                if (userPut == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        APIResponse(
                            "error",
                            ErrorModel(error = true, message = "not the right Parameters provided"),
                            false
                        )
                    )
                    return@put
                }
                val userService: UserService by closestDI().instance()
                call.respond(
                    APIResponse(userService.changeUser(user, userPut).toDto())
                )
            }
        }
    }
}

fun Route.allUsersRoute() {
    authenticate("auth-jwt-admin") {
        get("/users") {
            val userService: UserService by closestDI().instance()
            call.respond(
                APIResponse(userService.getAllUsers().map { it.toDto() })
            )
        }
    }
}

fun Route.userByIdRoute() {
    authenticate("auth-jwt-admin") {
        route("/users/{id}") {
            get("") {
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
        put("") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    APIResponse("error", ErrorModel(error = true, message = "path parameter is not a number"), false)
                )
                return@put
            }

            val userService: UserService by closestDI().instance()
            val user = userService.findUserByID(id)

            if (user == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    APIResponse("error", ErrorModel(error = true, message = "User not found"), false)
                )
                return@put
            }

            val userAdminPut: User.AdminPut? = call.receiveOrNull()

            if (userAdminPut == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    APIResponse("error", ErrorModel(error = true, message = "Send Object false"), false)
                )
                return@put
            }

            val updatedUser = userService.changeAdminUser(user, userAdminPut)
            call.respond(
                APIResponse(data = updatedUser.toDto())
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

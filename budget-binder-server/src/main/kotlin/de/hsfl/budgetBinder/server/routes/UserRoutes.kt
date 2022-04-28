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
            get {
                call.respond(APIResponse(data = call.principal<UserEntity>()!!.toDto(), success = true))
            }
            put {
                val user = call.principal<UserEntity>()!!
                val userService: UserService by closestDI().instance()

                val (status, response) = call.receiveOrNull<User.Put>()?.let { userPut ->
                    HttpStatusCode.OK to APIResponse(
                        data = userService.changeUser(user, userPut).toDto(),
                        success = true
                    )
                } ?: (HttpStatusCode.BadRequest to APIResponse(ErrorModel("not the right Parameters provided")))

                call.respond(status, response)
            }
            delete {
                val user = call.principal<UserEntity>()!!
                val userService: UserService by closestDI().instance()
                val response = APIResponse(data = user.toDto(), success = true)
                userService.deleteUser(user)
                call.respond(response)
            }
        }
    }
}

fun Route.allUsersRoute() {
    authenticate("auth-jwt-admin") {
        get("/users") {
            val userService: UserService by closestDI().instance()
            call.respond(
                APIResponse(data = userService.getAllUsers().map { it.toDto() }, success = true)
            )
        }
    }
}

private suspend fun getUserByIDOrErrorResponse(
    userService: UserService,
    id: Int?,
    callback: suspend (user: UserEntity) -> Pair<HttpStatusCode, APIResponse<User>>
): Pair<HttpStatusCode, APIResponse<User>> {
    return id?.let {
        userService.findUserByID(it)?.let { user ->
            callback(user)
        } ?: (HttpStatusCode.NotFound to APIResponse(ErrorModel("User not found")))
    } ?: (HttpStatusCode.BadRequest to APIResponse(ErrorModel("path parameter is not a number")))
}

fun Route.userByIdRoute() {
    authenticate("auth-jwt-admin") {
        route("/users/{id}") {
            get {
                val userService: UserService by closestDI().instance()

                val (status, response) = getUserByIDOrErrorResponse(
                    userService,
                    call.parameters["id"]?.toIntOrNull()
                ) { user ->
                    HttpStatusCode.OK to APIResponse(data = user.toDto(), success = true)
                }
                call.respond(status, response)
            }
        }
        put {
            val userService: UserService by closestDI().instance()

            val (status, response) = getUserByIDOrErrorResponse(
                userService,
                call.parameters["id"]?.toIntOrNull()
            ) { user ->
                call.receiveOrNull<User.AdminPut>()?.let { userAdminPut ->
                    HttpStatusCode.OK to APIResponse(
                        data = userService.changeAdminUser(user, userAdminPut).toDto(),
                        success = true
                    )
                } ?: (HttpStatusCode.NotFound to APIResponse(ErrorModel("Send Object false")))
            }
            call.respond(status, response)
        }
        delete {
            val userService: UserService by closestDI().instance()

            val (status, response) = getUserByIDOrErrorResponse(
                userService,
                call.parameters["id"]?.toIntOrNull()
            ) { user ->
                val response = APIResponse(data = user.toDto(), success = true)
                userService.deleteUser(user)
                HttpStatusCode.OK to response
            }
            call.respond(status, response)
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

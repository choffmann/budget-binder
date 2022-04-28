package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
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

                call.respond(call.receiveOrNull<User.Put>()?.let { userPut ->
                    APIResponse(data = userService.changeUser(user, userPut).toDto(), success = true)
                } ?: APIResponse(ErrorModel("not the right Parameters provided")))
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
            call.respond(APIResponse(data = userService.getAllUsers().map { it.toDto() }, success = true))
        }
    }
}

private suspend fun getUserByIDOrErrorResponse(
    userService: UserService,
    id: Int?,
    callback: suspend (user: UserEntity) -> APIResponse<User>
): APIResponse<User> {
    return id?.let {
        userService.findUserByID(it)?.let { user ->
            callback(user)
        } ?: APIResponse(ErrorModel("User not found"))
    } ?: APIResponse(ErrorModel("path parameter is not a number"))
}

fun Route.userByIdRoute() {
    authenticate("auth-jwt-admin") {
        route("/users/{id}") {
            get {
                val userService: UserService by closestDI().instance()

                call.respond(getUserByIDOrErrorResponse(userService, call.parameters["id"]?.toIntOrNull()) { user ->
                    APIResponse(data = user.toDto(), success = true)
                })
            }
        }
        put {
            val userService: UserService by closestDI().instance()

            call.respond(getUserByIDOrErrorResponse(userService, call.parameters["id"]?.toIntOrNull()) { user ->
                call.receiveOrNull<User.AdminPut>()?.let { userAdminPut ->
                    APIResponse(
                        data = userService.changeAdminUser(user, userAdminPut).toDto(),
                        success = true
                    )
                } ?: APIResponse(ErrorModel("Send Object false"))
            })
        }
        delete {
            val userService: UserService by closestDI().instance()

            call.respond(getUserByIDOrErrorResponse(
                userService,
                call.parameters["id"]?.toIntOrNull()
            ) { user ->
                val response = APIResponse(data = user.toDto(), success = true)
                userService.deleteUser(user)
                response
            })

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

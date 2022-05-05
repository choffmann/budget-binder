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

                val response = call.receiveOrNull<User.Put>()?.let { userPut ->
                    APIResponse(data = userService.changeUser(user, userPut).toDto(), success = true)
                } ?: APIResponse(ErrorModel("not the right Parameters provided"))

                call.respond(response)
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

// install all previous Routes
fun Application.userRoutes() {
    routing {
        meRoute()
    }
}

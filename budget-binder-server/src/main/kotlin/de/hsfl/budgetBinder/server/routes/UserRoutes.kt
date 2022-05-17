package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserPrincipal
import de.hsfl.budgetBinder.server.services.interfaces.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.meRoute() {
    authenticate("auth-jwt") {
        route("/me") {
            get {
                val userPrincipal: UserPrincipal = call.principal()!!
                val userService: UserService by closestDI().instance()
                call.respond(APIResponse(data = userService.findUserByID(userPrincipal.getUserID()), success = true))
            }
            patch {
                val userPrincipal: UserPrincipal = call.principal()!!
                val userService: UserService by closestDI().instance()

                val response = call.receiveOrNull<User.Put>()?.let { userPut ->
                    APIResponse(data = userService.changeUser(userPrincipal.getUserID(), userPut), success = true)
                } ?: APIResponse(ErrorModel("not the right Parameters provided"))

                call.respond(response)
            }
            delete {
                val userPrincipal: UserPrincipal = call.principal()!!
                val userService: UserService by closestDI().instance()
                call.respond(userService.deleteUser(userPrincipal.getUserID()))
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

package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserPrincipal
import de.hsfl.budgetBinder.server.services.interfaces.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Route.meRoute() {
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
            call.respond(APIResponse(data = userService.deleteUser(userPrincipal.getUserID()), success = true))
        }
    }
}

fun Application.userRoutes() {
    routing {
        authenticate("auth-jwt") {
            meRoute()
        }
    }
}

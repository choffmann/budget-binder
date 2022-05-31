package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.models.UserPrincipal
import de.hsfl.budgetBinder.server.repository.UnauthorizedException
import de.hsfl.budgetBinder.server.services.JWTService
import de.hsfl.budgetBinder.server.services.interfaces.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


fun Route.login() {
    authenticate("auth-form") {
        post("/login") {
            val userPrincipal: UserPrincipal = call.principal()!!
            val jwtService: JWTService by closestDI().instance()
            val token = jwtService.createAccessToken(userPrincipal.getUserID(), userPrincipal.getUserTokenVersion())

            call.response.cookies.append(
                jwtService.createRefreshCookie(
                    userPrincipal.getUserID(),
                    userPrincipal.getUserTokenVersion()
                )
            )
            call.respond(APIResponse(data = AuthToken(token = token), success = true))
        }
    }
}

fun Route.logout() {
    authenticate("auth-jwt") {
        get("/logout") {
            val logoutAll = call.request.queryParameters["all"].toBoolean()
            val userPrincipal: UserPrincipal = call.principal()!!

            if (logoutAll) {
                val userService: UserService by closestDI().instance()
                userService.logoutAllClients(userPrincipal.getUserID())
            }

            call.response.cookies.appendExpired("jwt", path = "/refresh_token")
            call.respond(APIResponse(data = AuthToken(""), success = true))
        }
    }
}

fun Route.refreshCookie() {
    get("/refresh_token") {
        val jwtService: JWTService by closestDI().instance()
        val userService: UserService by closestDI().instance()

        val response = call.request.cookies["jwt"]?.let { tokenToCheck ->
            val token = jwtService.getRefreshTokenVerifier().verify(tokenToCheck)
            val id = token.getClaim("userid").asInt()
            val tokenVersion = token.getClaim("token_version").asInt()
            userService.getUserPrincipalByIDAndTokenVersion(id, tokenVersion)?.let { userPrincipal ->
                val accessToken = jwtService.createAccessToken(userPrincipal.getUserID(), tokenVersion)
                call.response.cookies.append(
                    jwtService.createRefreshCookie(
                        userPrincipal.getUserID(),
                        userPrincipal.getUserTokenVersion()
                    )
                )
                APIResponse(data = AuthToken(token = accessToken), success = true)
            } ?: throw UnauthorizedException("Your refreshToken does not match.")
        } ?: throw UnauthorizedException("Your refreshToken is absent.")

        call.respond(response)
    }
}

fun Route.register() {
    post("/register") {
        val userService: UserService by closestDI().instance()

        val response = call.receiveOrNull<User.In>()?.let { userIn ->
            userService.insertNewUserOrNull(userIn)?.let { user ->
                APIResponse(data = user, success = true)
            } ?: APIResponse(ErrorModel("Email already assigned. Please choose another."))
        } ?: APIResponse(ErrorModel("The object you provided it not in the right format."))
        call.respond(response)
    }
}

fun Application.authRoutes() {
    routing {
        login()
        logout()
        refreshCookie()
        register()
    }
}

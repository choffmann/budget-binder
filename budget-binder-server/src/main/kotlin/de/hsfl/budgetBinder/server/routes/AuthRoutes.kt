package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.AuthToken
import de.hsfl.budgetBinder.common.ErrorModel
import de.hsfl.budgetBinder.common.User
import de.hsfl.budgetBinder.server.config.Config
import de.hsfl.budgetBinder.server.models.UserPrincipal
import de.hsfl.budgetBinder.server.services.JWTService
import de.hsfl.budgetBinder.server.services.interfaces.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.date.*
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SameSite
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun createRefreshCookie(token: String, timestamp: Long, sslState: Config.SSLState): Cookie {
    val secure: Boolean
    val sameSite: String
    when (sslState) {
        Config.SSLState.SSL,
        Config.SSLState.DEV -> {
            secure = true
            sameSite = SameSite.None.toString()
        }
        Config.SSLState.NONE -> {
            secure = false
            sameSite = SameSite.Lax.toString()
        }
    }
    return Cookie(
        "jwt",
        token,
        expires = GMTDate(timestamp),
        path = "/refresh_token",
        httpOnly = true,
        secure = secure,
        extensions = hashMapOf(SAMESITE to sameSite)
    )
}

fun Route.login() {
    authenticate("auth-form") {
        post("/login") {
            val userPrincipal: UserPrincipal = call.principal()!!
            val config: Config by closestDI().instance()

            val jwtService: JWTService by closestDI().instance()
            val token = jwtService.createAccessToken(userPrincipal.getUserID(), userPrincipal.getUserTokenVersion())
            val refreshToken =
                jwtService.createRefreshToken(userPrincipal.getUserID(), userPrincipal.getUserTokenVersion())

            call.response.cookies.append(
                createRefreshCookie(
                    refreshToken,
                    System.currentTimeMillis() + jwtService.getRefreshTokenValidationTime(),
                    config.server.sslState
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
            call.respond(HttpStatusCode.OK)
        }
    }
}

fun Route.refreshCookie() {
    get("/refresh_token") {
        val jwtService: JWTService by closestDI().instance()
        val userService: UserService by closestDI().instance()
        val config: Config by closestDI().instance()

        val (status, response) = call.request.cookies["jwt"]?.let { tokenToCheck ->
            val token = jwtService.getRefreshTokenVerifier().verify(tokenToCheck)
            val id = token.getClaim("userid").asInt()
            val tokenVersion = token.getClaim("token_version").asInt()
            userService.getUserPrincipalByIDAndTokenVersion(id, tokenVersion)?.let { userPrincipal ->
                val accessToken = jwtService.createAccessToken(userPrincipal.getUserID(), tokenVersion)
                val refreshToken = jwtService.createRefreshToken(userPrincipal.getUserID(), tokenVersion)
                call.response.cookies.append(
                    createRefreshCookie(
                        refreshToken,
                        System.currentTimeMillis() + jwtService.getRefreshTokenValidationTime(),
                        config.server.sslState
                    )
                )
                HttpStatusCode.OK to APIResponse(data = AuthToken(token = accessToken), success = true)
            } ?: (HttpStatusCode.Unauthorized to APIResponse(ErrorModel("Token Version is different")))
        } ?: (HttpStatusCode.Unauthorized to APIResponse(ErrorModel("No Refresh Cookie")))
        call.respond(status, response)
    }
}

fun Route.register() {
    post("/register") {
        val userService: UserService by closestDI().instance()

        val response = call.receiveOrNull<User.In>()?.let { userIn ->
            userService.insertNewUserOrNull(userIn)?.let { user ->
                APIResponse(data = user, success = true)
            } ?: APIResponse(ErrorModel("Email already assigned"))
        } ?: APIResponse(ErrorModel("not the right format"))
        call.respond(response)
    }
}

// install all previous Routes
fun Application.authRoutes() {
    routing {
        login()
        logout()
        refreshCookie()
        register()
    }
}

package de.hsfl.budgetBinder.server.routes

import de.hsfl.budgetBinder.common.APIResponse
import de.hsfl.budgetBinder.common.Post
import de.hsfl.budgetBinder.server.models.UserEntity
import de.hsfl.budgetBinder.server.services.JWTService
import de.hsfl.budgetBinder.server.services.UserService
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.date.*
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SAMESITE
import io.netty.handler.codec.http.cookie.CookieHeaderNames.SameSite
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun createRefreshCookie(token: String, timestamp: Long): Cookie {
    val secure = System.getenv("DEV") != "True"
    val sameSiteValue = if (secure) SameSite.None.toString() else SameSite.Lax.toString()

    return Cookie(
        "jwt",
        token,
        expires = GMTDate(timestamp),
        path = "/refresh_token",
        httpOnly = true,
        secure = secure,
        extensions = hashMapOf(SAMESITE to sameSiteValue)
    )
}

fun Route.login() {
    authenticate("auth-form") {
        post("/login") {
            val user = call.principal<UserEntity>()!!

            val jwtService: JWTService by closestDI().instance()
            val token = jwtService.createAccessToken(user.id.value, user.tokenVersion)
            val refreshToken = jwtService.createRefreshToken(user.id.value, user.tokenVersion)

            call.response.cookies.append(
                createRefreshCookie(
                    refreshToken,
                    System.currentTimeMillis() + jwtService.getRefreshTokenValidationTime()
                )
            )
            call.respond(APIResponse(hashMapOf("token" to token)))
        }
    }
}

fun Route.refreshCookie() {
    get("/refresh_token") {
        val tokenToCheck = call.request.cookies["jwt"]

        if (tokenToCheck == null) {
            call.respond(
                HttpStatusCode.Unauthorized,
                APIResponse<Post>(null, "No Refresh Cookie", false)
            )
            return@get
        }
        val jwtService: JWTService by closestDI().instance()
        val token = jwtService.getRefreshTokenVerifier().verify(tokenToCheck)

        val id = token.getClaim("userid").asInt()
        val tokenVersion = token.getClaim("token_version").asInt()
        val userService: UserService by closestDI().instance()
        val user = userService.findUserByID(id)

        if (user?.tokenVersion != tokenVersion) {
            call.respond(
                HttpStatusCode.Unauthorized,
                APIResponse<String>(null, "Token Version is different", false)
            )
            return@get
        }

        val accessToken = jwtService.createAccessToken(id, tokenVersion)

        val refreshToken = jwtService.createRefreshToken(id, tokenVersion)

        call.response.cookies.append(
            createRefreshCookie(
                refreshToken,
                System.currentTimeMillis() + jwtService.getRefreshTokenValidationTime()
            )
        )
        call.respond(APIResponse(hashMapOf("token" to accessToken)))
    }
}

// install all previous Routes
fun Application.authRoutes() {
    routing {
        login()
        refreshCookie()
    }
}
